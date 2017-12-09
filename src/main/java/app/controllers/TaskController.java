package app.controllers;

import app.entities.Task;
import app.entities.enums.TicketStatus;
import app.services.TaskService;
import app.validation.tags.TaskDependenciesDbValidation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;


@Controller
public class TaskController
{

    private final TaskService taskService;

    private final Log logger = LogFactory.getLog( getClass() );

    @Autowired
    public TaskController ( TaskService taskService )
    {
        this.taskService = taskService;
    }

    @RequestMapping ( value = "api/task/newtask", method = RequestMethod.POST )
    public ResponseEntity submitTask ( @Valid @Validated ( value = TaskDependenciesDbValidation.class )
                                       @RequestBody Task task )
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

    @RequestMapping ( value = "/api/task/changestatus", method = RequestMethod.POST )
    public ResponseEntity changeStatus ( @RequestParam ( "ticketId" ) Long ticketId,
                                         @RequestParam ( "status" ) TicketStatus status )
    {
        this.taskService.changeTicketStatus( ticketId, status );
        return new ResponseEntity<>( HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/task/byticket/{id}", method = RequestMethod.GET )
    public ResponseEntity getByTicketId ( @PathVariable ( "id" ) Long id )
    {
        Task task = this.taskService.getTaskByTicketId( id );
        return new ResponseEntity<>( task, HttpStatus.OK );
    }
}
