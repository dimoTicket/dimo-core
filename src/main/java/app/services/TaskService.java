package app.services;

import app.entities.Task;
import app.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TaskService
{

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask ( Task task )
    {
        return this.taskRepository.save( task );
    }

    public boolean taskExists ( Task task )
    {
        return this.taskRepository.findByTicket( task.getTicket() ).isPresent();
    }
}
