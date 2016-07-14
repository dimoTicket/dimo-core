package unit;

import app.DimoApplication;
import app.controllers.TaskController;
import app.entities.Task;
import app.entities.Ticket;
import app.entities.User;
import app.entities.enums.TicketStatus;
import app.exceptions.RestExceptionHandler;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UserServiceException;
import app.services.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
@ContextConfiguration ( classes = MockServletContext.class )
@WebAppConfiguration
public class TaskControllerTests
{

    @InjectMocks
    private TaskController taskController;

    @Mock
    TaskService taskService;

    private MockMvc mockMvc;

    private Task task;

    private Ticket ticket;

    private User user;

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );
        mockMvc = standaloneSetup( this.taskController )
                .setControllerAdvice( new RestExceptionHandler() )
                .build();

        ticket = new Ticket();
        ticket.setId( 1L );
        ticket.setMessage( "Ticket message 1" );
        ticket.setImageName( "Test image name 1" );
        ticket.setLatitude( 12.345678 );
        ticket.setLongitude( 25.579135 );
        ticket.setStatus( TicketStatus.ASSIGNED );

        user = new User();
        user.setId( 1L );
        user.setUsername( "MockName" );
        List<User> users = new ArrayList<>();
        users.add( user );

        task = new Task();
        task.setId( 1L );
        task.setTicket( ticket );
        task.setUsers( users );
    }

    @Test
    public void getAllTasksRest () throws Exception
    {
        Answer<List<Task>> answer = invocation ->
        {
            List<Task> tasks = new ArrayList<>();
            tasks.add( this.task );
            return tasks;
        };
        when( taskService.getAll() ).thenAnswer( answer );
        mockMvc.perform( get( "/api/tasks" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) )
                .andExpect( ( jsonPath( "$.[0].id" ).value( this.task.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.[0].createdAt" ).isNotEmpty() ) )
                .andExpect( ( jsonPath( "$..ticket.id" ).value( this.ticket.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$..ticket.message" ).value( this.ticket.getMessage() ) ) )
                .andExpect( ( jsonPath( "$..users" ).isArray() ) )
                .andExpect( ( jsonPath( "$..users[0].id" ).value( this.user.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$..users[0].username" ).value( this.user.getUsername() ) ) )
                .andExpect( ( jsonPath( "$..users[0].password" ).doesNotExist() ) )
        ;
    }

    @Test
    public void getAllTasksRestWhenNoTasksExist () throws Exception
    {
        Answer<List<Task>> answer = invocation -> new ArrayList<>();
        when( taskService.getAll() ).thenAnswer( answer );
        mockMvc.perform( get( "/api/tasks" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) )
        ;
    }

    @Test
    public void getTaskByIdRest () throws Exception
    {
        when( taskService.getById( this.task.getId() ) ).thenReturn( this.task );
        mockMvc.perform( get( "/api/task/" + this.task.getId() ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( this.task.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "createdAt" ).isNotEmpty() ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( this.ticket.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.ticket.message" ).value( this.ticket.getMessage() ) ) )
                .andExpect( ( jsonPath( "$.users" ) ).isArray() )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( this.user.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( this.user.getUsername() ) ) )
                .andExpect( ( jsonPath( "$.users[0].password" ).doesNotExist() ) )
        ;
    }

    @Test
    public void getTaskByIdRestForTaskThatDoesNotExist () throws Exception
    {
        when( taskService.getById( this.task.getId() ) ).thenThrow( new ResourceNotFoundException() );
        mockMvc.perform( get( "/api/task/" + this.task.getId() ) )
                .andExpect( ( status().isNotFound() ) )
        ;
    }

    @Test
    public void submitTask () throws Exception
    {
        when( taskService.create( any( Task.class ) ) ).thenReturn( this.task );

        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  \"longitude\": 22.951907}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"username\": \"MockName\"\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isCreated() )
                .andExpect( header().string( "location", "http://localhost/api/task/1" ) );
    }

    @Test
    public void submitTaskForTicketThatDoesNotExist () throws Exception
    {
        when( taskService.create( any( Task.class ) ) ).thenThrow( new ResourceNotFoundException() );

        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  \"longitude\": 22.951907}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"username\": \"MockName\"\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    public void submitTaskForTicketThatAlreadyHasATask () throws Exception
    {
        when( taskService.create( any( Task.class ) ) ).thenThrow( new BadRequestException() );

        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  \"longitude\": 22.951907}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"username\": \"MockName\"\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void submitTaskWithUserThatDoesNotExist () throws Exception
    {
        //// FIXME: 12/7/2016 The implementation of create throws UserNameNotFoundEx which extends UserServiceEx
        //// FIXME: Using UserNameNotFoundEx won't work in mockmvc but works on runtime
        doThrow( new UserServiceException( "" ) ).when( this.taskService ).create( any( Task.class ) );

        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  \"longitude\": 22.951907}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"username\": \"MockName\"\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isInternalServerError() );
    }

    @Test
    public void submitTaskMalformedRequest () throws Exception
    {
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"malformed\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"malformed\": \"MockName\"\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void submitTaskWithoutUsers () throws Exception
    {
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 12.131313," +
                        "\"longitude\": 14.141414}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void submitTaskWithEmptyUsers () throws Exception
    {
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  \"longitude\": 22.951907}, \n" +
                        "  \"users\": []\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );
    }
}
