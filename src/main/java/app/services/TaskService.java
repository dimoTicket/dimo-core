package app.services;

import app.entities.Task;
import app.entities.User;
import app.entities.enums.TicketStatus;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UserIdDoesNotExistException;
import app.repositories.TaskRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


@Service
public class TaskService implements app.services.Service
{

    private final Log logger = LogFactory.getLog( getClass() );

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    public Task create ( Task task )
    {
        this.ticketService.verifyTicketExists( task.getTicket().getId() );

        if ( this.taskExistsForTicketId( task.getTicket().getId() ) )
        {
            throw new BadRequestException( "A task already exists for ticket with id : " + task.getTicket().getId() );
        }

        task.getUsers().forEach( user ->
        {
            if ( !userService.userExists( user.getId() ) )
            {
                throw new UserIdDoesNotExistException( "User with id " + user.getId() + " not found" );
            }
        } );

        //Refreshing so that all associations in Task are populated from the database,
        //since we only have the IDs
        task = this.taskRepository.saveFlushAndRefresh( task );

        this.changeTicketStatus( task.getTicket().getId(), TicketStatus.ASSIGNED );
        return task;
    }

    public Task addUsersToTask ( Task task )
    {
        Collection<User> inUsers = task.getUsers();
        Task taskFromDb = this.getById( task.getId() );
        inUsers.forEach( ( inUser ->
        {
            if ( !this.userService.userExists( inUser.getId() ) )
            {
                throw new UserIdDoesNotExistException( "User with id " + inUser.getId() + " not found" );
            }
            if ( taskFromDb.getUsers().contains( inUser ) )
            {
                logger.info( "User: " + inUser.getUsername() + " was already assigned to task with id: " + taskFromDb.getId() );
            } else
            {
                logger.info( "Adding user: " + inUser.getId() + " to task with id: " + taskFromDb.getId() );
                taskFromDb.getUsers().add( inUser );
            }
        } ) );
        return this.taskRepository.save( taskFromDb );
    }

    @Transactional
    public Task removeUsersFromTask ( Task task )
    {
        Collection<User> inUsers = task.getUsers();
        Task taskFromDb = this.getById( task.getId() );
        inUsers.forEach( ( user ->
        {
            if ( taskFromDb.getUsers().contains( user ) )
            {
                logger.info( "Removing user: " + user.getId() + " from task with id: " + taskFromDb.getId() );
                this.taskRepository.removeUserFromTask( task.getId(), user.getId() );
//                taskFromDb.getUsers().remove( user );
            } else
            {
                logger.info( "User: " + user.getId() + " not found in task with id: " + taskFromDb.getId() );
            }
        } ) );
        return this.taskRepository.getOne( taskFromDb.getId() );
//        return this.taskRepository.save( taskFromDb );
    }

    public void changeTicketStatus ( Long ticketId, TicketStatus status )
    {
        this.ticketService.changeStatus( ticketId, status );
    }

    public Task getById ( Long taskId )
    {
        this.verifyTaskExists( taskId );
        return this.taskRepository.findOne( taskId );
    }

    public List<Task> getAll ()
    {
        return this.taskRepository.findAll();
    }

    public Task getTaskByTicketId ( Long ticketId )
    {
        return this.taskRepository.findByTicketId( ticketId )
                .orElseThrow( () -> new ResourceNotFoundException( "There is no Task for given ticket with id : " + ticketId ) );
    }

    public boolean taskExistsForTicketId ( Long ticketId )
    {
        return this.taskRepository.findByTicketId( ticketId ).isPresent();
    }

    public void verifyTaskExists ( Long taskId ) throws ResourceNotFoundException
    {
        Task task = this.taskRepository.findOne( taskId );
        if ( task == null )
        {
            throw new ResourceNotFoundException( "Task with id " + taskId + " not found" );
        }
    }
}
