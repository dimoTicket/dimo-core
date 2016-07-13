package app.controllers;

import app.entities.Task;
import app.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @Autowired
    private TaskService taskService;

    @RequestMapping ( value = "api/task/newtask", method = RequestMethod.POST )
    public ResponseEntity submitTask ( @Valid @RequestBody Task task )
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
    public ResponseEntity getAllTicketsRest ()
    {
        return new ResponseEntity<>( this.taskService.getAll(), HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/task/{id}/addusers", method = RequestMethod.POST )
    public ResponseEntity addUsersToTask ( @PathVariable ( "id" ) Long id, @Valid @RequestBody Task task )
    {
        this.taskService.addUsersToTask( task );
        return new ResponseEntity<>( HttpStatus.OK );
    }

}
