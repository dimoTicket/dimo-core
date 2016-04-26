package app.controllers;

import app.entities.Task;
import app.entities.enums.TicketStatus;
import app.repositories.TaskRepository;
import app.services.TicketService;
import app.services.UserService;
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
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TaskRepository taskRepository;

    @RequestMapping ( value = "api/task/newtask", method = RequestMethod.POST )
    public ResponseEntity submitTask ( @Valid @RequestBody Task task )
    {
        this.taskRepository.save( task );
        this.ticketService.changeTicketStatus( task.getTicket(), TicketStatus.ASSIGNED );

        HttpHeaders httpResponseHeaders = new HttpHeaders();
        URI newTaskUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path( "/api/{id}" ).buildAndExpand( task.getId() ).toUri();
        httpResponseHeaders.setLocation( newTaskUri );
        return new ResponseEntity<>( httpResponseHeaders, HttpStatus.CREATED );
    }

    @RequestMapping ( value = "/api/task/{id}", method = RequestMethod.GET )
    public ResponseEntity getTaskById ( @PathVariable ( "id" ) Long id )
    {
//        verify task
        Task task = this.taskRepository.findOne( id );
        return new ResponseEntity<>( task, HttpStatus.OK );
    }

}
