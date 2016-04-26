package app.services;

import app.entities.Task;
import app.entities.Ticket;
import app.exceptions.service.ResourceNotFoundException;
import app.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskService
{

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask ( Task task )
    {
        // TODO: 26/04/2016 Throw if there's already an !active! task for the given ticket
        return this.taskRepository.save( task );
    }

    public Task getById ( Long taskId )
    {
        return this.taskRepository.findOne( taskId );
    }
    public List<Task> getAllTasks ()
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
}
