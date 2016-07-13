package app.services;

import app.entities.Task;
import app.entities.Ticket;
import app.entities.User;
import app.entities.enums.TicketStatus;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UsernameDoesNotExistException;
import app.repositories.TaskRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
public class TaskService
{

    private final Log logger = LogFactory.getLog( getClass() );

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    public Task create ( Task task )
    {
        this.ticketService.verifyTicketExists( task.getTicket().getId() );

        if ( this.taskExistsForTicket( task.getTicket() ) )
        {
            throw new BadRequestException( "A task already exists for ticket with id : " + task.getTicket().getId() );
        }

        task.getUsers().parallelStream().forEach( user ->
        {
            if ( !this.userService.userExists( user.getUsername() ) )
            {
                throw new UsernameDoesNotExistException( "Username :" + user.getUsername() + " does not exist" );
            }
        } );
        task = this.taskRepository.save( task );
        this.ticketService.changeStatus( task.getTicket(), TicketStatus.ASSIGNED );
        return task;
    }

    public Task addUsersToTask ( Task task )
    {
        Collection<User> inUsers = task.getUsers();
        Task taskFromDb = this.getById( task.getId() );
        inUsers.stream()
                .peek( user ->
                {
                    if ( !this.userService.userExists( user.getUsername() ) )
                    {
                        throw new UsernameDoesNotExistException( "Username :" + user.getUsername() + " not found in the system" );
                    }
                } )
                .forEach( ( user ->
                {
                    if ( taskFromDb.getUsers().contains( user ) )
                    {
                        logger.info( "User: " + user.getUsername() + " was already assigned to task with id: " + taskFromDb.getId() );
                    } else
                    {
                        logger.info( "Adding user: " + user.getUsername() + " to task with id: " + taskFromDb.getId() );
                        taskFromDb.getUsers().add( user );
                    }
                } ) );
        return this.taskRepository.save( taskFromDb );
    }

    public Task removeUsersFromTask ( Task task )
    {
        Collection<User> inUsers = task.getUsers();
        Task taskFromDb = this.getById( task.getId() );
        inUsers.forEach( ( user ->
        {
            if ( taskFromDb.getUsers().contains( user ) )
            {
                taskFromDb.getUsers().remove( user );
            } else
            {
                logger.info( "User: " + user.getUsername() + " not found in task with id: " + taskFromDb.getId() );
            }
        } ) );
        return this.taskRepository.save( taskFromDb );
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

    public Task getTaskForTicket ( Ticket ticket )
    {
        return this.taskRepository.findByTicket( ticket )
                .orElseThrow( () -> new ResourceNotFoundException( "There is no Task for given ticket with id : " + ticket.getId() ) );
    }

    public boolean taskExistsForTicket ( Ticket ticket )
    {
        return this.taskRepository.findByTicket( ticket ).isPresent();
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
