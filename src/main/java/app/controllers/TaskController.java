package app.controllers;

import app.entities.Task;
import app.services.TaskService;
import app.validation.tags.TaskDependenciesDbValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;


@Controller
public class TaskController
{

    private final TaskService taskService;

    @Autowired
    public TaskController ( TaskService taskService )
    {
        this.taskService = taskService;
    }

    @RequestMapping ( value = "api/task/newtask", method = RequestMethod.POST )
    public ResponseEntity submitTask ( @Valid @Validated ( value = TaskDependenciesDbValidation.class ) @RequestBody Task task )
    {
        task = this.taskService.create( task );

        HttpHeaders httpResponseHeaders = new HttpHeaders();
        URI newTaskUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path( "/api/task/{id}" ).buildAndExpand( task.getId() ).toUri();
        httpResponseHeaders.setLocation( newTaskUri );
        return new ResponseEntity<>( httpResponseHeaders, HttpStatus.CREATED );
    }

    @RequestMapping ( value = "/api/task/{id}" )
    public ResponseEntity getTaskById ( @PathVariable ( "id" ) Long id )
    {
        Task task = this.taskService.getById( id );
        return new ResponseEntity<>( task, HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/tasks" )
    public ResponseEntity getAllTasksRest ()
    {
        return new ResponseEntity<>( this.taskService.getAll(), HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/task/addusers", method = RequestMethod.POST )
    public ResponseEntity addUsersToTask ( @Validated ( value = TaskDependenciesDbValidation.class )
                                           @RequestBody Task task )
    {
        this.taskService.addUsersToTask( task );
        return new ResponseEntity<>( HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/task/removeusers", method = RequestMethod.POST )
    public ResponseEntity removeUsersFromTask ( @Validated ( value = TaskDependenciesDbValidation.class )
                                                @RequestBody Task task )
    {
        this.taskService.removeUsersFromTask( task );
        return new ResponseEntity<>( HttpStatus.OK );
    }

}
