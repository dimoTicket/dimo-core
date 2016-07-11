package app.services;

import app.entities.Task;
import app.entities.Ticket;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UsernameDoesNotExistException;
import app.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskService
{

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

        task.getUsers().parallelStream().forEach( user -> {
            if ( !this.userService.userExists( user.getUsername() ) )
            {
                throw new UsernameDoesNotExistException( "Username :" + user.getUsername() + " does not exist" );
            }
        } );

        return this.taskRepository.save( task );
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
