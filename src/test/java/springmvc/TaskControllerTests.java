package springmvc;

import app.controllers.TaskController;
import app.entities.Task;
import app.entities.Ticket;
import app.entities.User;
import app.entities.enums.TicketStatus;
import app.exceptions.RestExceptionHandler;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UserIdDoesNotExistException;
import app.exceptions.service.UserServiceException;
import app.services.Service;
import app.services.TaskService;
import app.services.TicketService;
import app.services.UserService;
import app.validation.TestConstraintValidationFactory;
import app.validation.TicketExistsValidator;
import app.validation.UsersExistValidator;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith ( MockitoJUnitRunner.class )
@ActiveProfiles ( "unit-tests" )
public class TaskControllerTests
{

    @Autowired
    private MockServletContext servletContext;

    @Mock
    private TaskService taskService;

    @Mock
    private TicketService ticketService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private Task task;

    private Ticket ticket;

    private User user;

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );
        createMockTask();

        LocalValidatorFactoryBean validatorFactoryBean = getCustomValidatorFactoryBean();
        mockMvc = standaloneSetup( new TaskController( this.taskService ) )
                .setValidator( validatorFactoryBean )
                .setControllerAdvice( new RestExceptionHandler() )
                .build();

    }

    private LocalValidatorFactoryBean getCustomValidatorFactoryBean ()
    {
        final GenericWebApplicationContext context = new GenericWebApplicationContext( servletContext );
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        beanFactory.registerSingleton( TicketExistsValidator.class.getCanonicalName(), new TicketExistsValidator() );
        beanFactory.registerSingleton( UsersExistValidator.class.getCanonicalName(), new UsersExistValidator() );

        context.refresh();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setApplicationContext( context );

        List<Service> servicesUsedByValidators = new ArrayList<>();
        servicesUsedByValidators.add( this.ticketService );
        servicesUsedByValidators.add( this.userService );
        TestConstraintValidationFactory constraintFactory =
                new TestConstraintValidationFactory( context, servicesUsedByValidators );

        validatorFactoryBean.setConstraintValidatorFactory( constraintFactory );
        validatorFactoryBean.setProviderClass( HibernateValidator.class );
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
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
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        when( userService.loadById( 1L ) ).thenReturn( this.user );

        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1},\n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1\n" +
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
                .content( "{\"ticket\": {\"id\": 1}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1}\n" +
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
                .content( "{\"ticket\": {\"id\": 1}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1}\n" +
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
                .content( "{\"ticket\": {\"id\": 1}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1}\n" +
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
                .content( "{\"ticket\": {\"id\": 1},\n" +
                        " \"users\": []\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );
    }

    //User with id 2L will be added to the task
    @Test
    public void addUsersToTask () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser = new User();
        newUser.setId( 2L );
        newUser.setUsername( "MockName2" );
        when( userService.loadById( 2L ) ).thenReturn( newUser );

        this.task.getUsers().add( newUser );
        when( this.taskService.addUsersToTask( this.task ) ).thenReturn( this.task );

        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isOk() );
    }

    @Test
    public void addUsersToTaskForTaskThatDoesNotExist () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser = new User();
        newUser.setId( 2L );
        newUser.setUsername( "MockName2" );
        when( userService.loadById( 2L ) ).thenReturn( newUser );

        this.task.getUsers().add( newUser );
        when( this.taskService.addUsersToTask( this.task ) ).thenThrow( new ResourceNotFoundException() );

        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    public void addUsersToTaskWhenAllGivenUsersDontExist () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser2 = new User();
        newUser2.setId( 2L );
        newUser2.setUsername( "MockName2" );
        User newUser3 = new User();
        newUser3.setId( 3L );
        newUser3.setUsername( "MockName3" );
        when( userService.loadById( any( Long.class ) ) ).thenThrow( new UserIdDoesNotExistException() );

        this.task.getUsers().add( newUser2 );
        this.task.getUsers().add( newUser3 );
        //User validation on user service level won't be invoked therefore no need to mock it.

        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void addUsersToTaskWhenSomeGivenUsersDontExist () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser2 = new User();
        newUser2.setId( 2L );
        newUser2.setUsername( "MockName2" );
        User newUser3 = new User();
        newUser3.setId( 3L );
        newUser3.setUsername( "MockName3" );
        when( userService.loadById( 2L ) ).thenReturn( newUser2 );
        when( userService.loadById( 3L ) ).thenThrow( new UserIdDoesNotExistException() );

        this.task.getUsers().add( newUser2 );
        this.task.getUsers().add( newUser3 );
        //User validation on user service level won't be invoked therefore no need to mock it.

        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void addUsersToTaskWhenUsersAlreadyInTask () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser2 = new User();
        newUser2.setId( 2L );
        newUser2.setUsername( "MockName2" );
        User newUser3 = new User();
        newUser3.setId( 3L );
        newUser3.setUsername( "MockName3" );
        when( userService.loadById( 2L ) ).thenReturn( newUser2 );
        when( userService.loadById( 3L ) ).thenReturn( newUser3 );

        this.task.getUsers().add( newUser2 );
        this.task.getUsers().add( newUser3 );
        when( this.taskService.addUsersToTask( this.task ) ).thenReturn( this.task );

        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isOk() );
    }

    @Test
    public void addUsersToTaskEmptyGivenUserArray () throws Exception
    {
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": []}" ) )
                .andExpect( status().isOk() );
    }

    @Test
    public void addUsersToTaskAbsentGivenUserArray () throws Exception
    {
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1}}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void removeUsersFromTask () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser = new User();
        newUser.setId( 2L );
        newUser.setUsername( "MockName2" );
        when( userService.loadById( 2L ) ).thenReturn( newUser );

        this.task.getUsers().add( newUser );
        when( this.taskService.removeUsersFromTask( this.task ) ).thenReturn( this.task );

        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isOk() );
    }

    @Test
    public void removeUsersfromTaskForTaskThatDoesNotExist () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser = new User();
        newUser.setId( 2L );
        newUser.setUsername( "MockName2" );
        when( userService.loadById( 2L ) ).thenReturn( newUser );

        this.task.getUsers().add( newUser );
        when( this.taskService.removeUsersFromTask( this.task ) ).thenThrow( new ResourceNotFoundException() );

        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    public void removeUsersFromTaskWhenAllGivenUsersDontExist () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser2 = new User();
        newUser2.setId( 2L );
        newUser2.setUsername( "MockName2" );
        User newUser3 = new User();
        newUser3.setId( 3L );
        newUser3.setUsername( "MockName3" );
        when( userService.loadById( any( Long.class ) ) ).thenThrow( new UserIdDoesNotExistException() );

        this.task.getUsers().add( newUser2 );
        this.task.getUsers().add( newUser3 );
        //User validation on user service level won't be invoked therefore no need to mock it.

        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void removeUsersFromTaskWhenSomeGivenUsersDontExist () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser2 = new User();
        newUser2.setId( 2L );
        newUser2.setUsername( "MockName2" );
        User newUser3 = new User();
        newUser3.setId( 3L );
        newUser3.setUsername( "MockName3" );
        when( userService.loadById( 2L ) ).thenReturn( newUser2 );
        when( userService.loadById( 3L ) ).thenThrow( new UserIdDoesNotExistException() );

        this.task.getUsers().add( newUser2 );
        this.task.getUsers().add( newUser3 );
        //User validation on user service level won't be invoked therefore no need to mock it.

        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void removeUsersFromTaskWhenUsersAlreadyInTask () throws Exception
    {
        //validator services mocking
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        User newUser2 = new User();
        newUser2.setId( 2L );
        newUser2.setUsername( "MockName2" );
        User newUser3 = new User();
        newUser3.setId( 3L );
        newUser3.setUsername( "MockName3" );
        when( userService.loadById( 2L ) ).thenReturn( newUser2 );
        when( userService.loadById( 3L ) ).thenReturn( newUser3 );

        this.task.getUsers().add( newUser2 );
        this.task.getUsers().add( newUser3 );
        when( this.taskService.removeUsersFromTask( this.task ) ).thenReturn( this.task );

        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isOk() );
    }

    @Test
    public void removeUsersFromTaskEmptyGivenUserArray () throws Exception
    {
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": []}" ) )
                .andExpect( status().isOk() );
    }

    @Test
    public void removeUsersFromTaskAbsentGivenUserArray () throws Exception
    {
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1}}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void changeStatus () throws Exception
    {
        doNothing().when( this.taskService ).changeTicketStatus( any(), any() );
        mockMvc.perform( post( "/api/task/changestatus" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .param( "ticketId", ticket.getId().toString() )
                .param( "status", TicketStatus.REJECTED.toString() ) )
                .andExpect( status().isOk() );

        verify( taskService ).changeTicketStatus( ticket.getId(), TicketStatus.REJECTED );
    }

    @Test
    public void getByTicketId () throws Exception
    {
        when( this.taskService.getTaskByTicketId( 1L ) ).thenReturn( this.task );
        mockMvc.perform( get( "/api/task/byticket/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( this.task.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "createdAt" ).isNotEmpty() ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( this.ticket.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.ticket.message" ).value( this.ticket.getMessage() ) ) )
                .andExpect( ( jsonPath( "$.users" ) ).isArray() )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( this.user.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( this.user.getUsername() ) ) )
                .andExpect( ( jsonPath( "$.users[0].password" ).doesNotExist() ) );
    }

    @Test
    public void getByTicketIdWhenTaskDoesNotExist () throws Exception
    {
        when( this.taskService.getTaskByTicketId( 1L ) ).thenThrow( new ResourceNotFoundException() );
        mockMvc.perform( get( "/api/task/byticket/1" ) )
                .andExpect( status().isNotFound() );
    }

    private void createMockTask ()
    {
        ticket = new Ticket();
        ticket.setId( 1L );
        ticket.setMessage( "Ticket message 1" );
        ticket.setImages( new ArrayList<>() );
        ticket.setLatitude( 12.345678 );
        ticket.setLongitude( 25.579135 );
        ticket.setStatus( TicketStatus.ASSIGNED );

        user = new User();
        user.setId( 1L );
        user.setUsername( "MockName" );
        Set<User> users = new HashSet<>();
        users.add( user );

        task = new Task();
        task.setId( 1L );
        task.setTicket( ticket );
        task.setUsers( users );
    }
}
