package unit;

import app.DimoApplication;
import app.entities.Task;
import app.entities.Ticket;
import app.entities.User;
import app.entities.enums.TicketStatus;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UsernameDoesNotExistException;
import app.repositories.TaskRepository;
import app.services.TaskService;
import app.services.TicketService;
import app.services.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
public class TaskServiceTests
{

    @Autowired
    @InjectMocks
    private TaskService taskService;

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserService userService;

    @Mock
    TicketService ticketService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void create ()
    {
        Task task = this.getMockTask();
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.empty() );
        when( this.userService.userExists( any( String.class ) ) ).thenReturn( true );
        Answer<Task> saveAnswer = invocation ->
        {
            task.setId( 1L );
            return task;
        };
        when( this.taskRepository.save( any( Task.class ) ) ).thenAnswer( saveAnswer );
        doNothing().when( this.ticketService ).changeStatus( any( Ticket.class ), any( TicketStatus.class ) );

        this.taskService.create( task );
    }

    @Test
    public void createForTicketThatDoesNotExist ()
    {
        Task task = this.getMockTask();
        doThrow( new ResourceNotFoundException( "" ) ).when( this.ticketService ).verifyTicketExists( any( Long.class ) );

        this.thrown.expect( ResourceNotFoundException.class );
        this.taskService.create( task );
    }

    @Test
    public void createForTicketThatAlreadyHasATask ()
    {
        Task task = this.getMockTask();
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.of( new Task() ) );
        when( this.userService.userExists( any( String.class ) ) ).thenReturn( true );

        this.thrown.expect( BadRequestException.class );
        this.taskService.create( task );
    }

    @Test
    public void createWhenAllUsersDontExist ()
    {
        Task task = this.getMockTask();
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.empty() );
        when( this.userService.userExists( any( String.class ) ) ).thenReturn( false );

        this.thrown.expect( UsernameDoesNotExistException.class );
        this.taskService.create( task );
    }

    @Test
    public void createWhenOneUserDoesNotExist ()
    {
        Task task = this.getMockTask();
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.empty() );
        when( this.userService.userExists( "MockUser" ) ).thenReturn( false );
        when( this.userService.userExists( "MockUser2" ) ).thenReturn( true );

        this.thrown.expect( UsernameDoesNotExistException.class );
        this.taskService.create( task );
    }

    @Test
    public void getTaskForTicket ()
    {
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.of( new Task() ) );
        Task taskForTicket = this.taskService.getTaskForTicket( new Ticket() );
        assertThat( taskForTicket, is( not( nullValue() ) ) );
    }

    @Test
    public void getTaskForTicketThatDoesNotExist ()
    {
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.empty() );
        this.thrown.expect( ResourceNotFoundException.class );
        this.taskService.getTaskForTicket( new Ticket() );
    }

    @Test
    public void taskExistsForTicket ()
    {
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.of( new Task() ) );
        assertThat( this.taskService.taskExistsForTicket( new Ticket() ), is( true ) );
    }

    @Test
    public void taskExistsForTicketThatDoesNotExist ()
    {
        when( this.taskRepository.findByTicket( any( Ticket.class ) ) ).thenReturn( Optional.empty() );
        assertThat( this.taskService.taskExistsForTicket( new Ticket() ), is( false ) );
    }

    @Test
    public void addUsersToTask ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();
        User user3 = new User();
        user3.setId( 3L );
        user3.setUsername( "MockUser3" );
        inTask.getUsers().add( user3 );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.userService.userExists( "MockUser3" ) ).thenReturn( true );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 3 ) );
        assertThat( dbTask.getUsers().toArray()[ 2 ], is( user3 ) );
    }

    @Test
    public void addUsersToTaskMultipleInvocations ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();
        User user3 = new User();
        user3.setId( 3L );
        user3.setUsername( "MockUser3" );
        inTask.getUsers().add( user3 );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.userService.userExists( "MockUser3" ) ).thenReturn( true );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 3 ) );
        assertThat( dbTask.getUsers().toArray()[ 2 ], is( user3 ) );
    }

    @Test
    public void addUsersToTaskEmptyUserList ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();
        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 2 ) );
    }

    @Test
    public void addUsersToTaskUserDoesNotExist ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();
        User user3 = new User();
        user3.setId( 3L );
        user3.setUsername( "MockUser3" );
        inTask.getUsers().add( user3 );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.userService.userExists( "MockUser3" ) ).thenThrow( new UsernameDoesNotExistException( "" ) );
        thrown.expect( UsernameDoesNotExistException.class );
        this.taskService.addUsersToTask( inTask );
    }

    @Test
    public void addUsersToTaskSomeUsersDontExist ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();
        User user3 = new User();
        user3.setId( 3L );
        user3.setUsername( "MockUser3" );
        User user4 = new User();
        user4.setId( 4L );
        user4.setUsername( "MockUser4" );
        inTask.getUsers().add( user3 );
        inTask.getUsers().add( user4 );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.userService.userExists( "MockUser3" ) ).thenReturn( true );
        when( this.userService.userExists( "MockUser4" ) ).thenThrow( new UsernameDoesNotExistException( "" ) );
        thrown.expect( UsernameDoesNotExistException.class );
        dbTask = this.taskService.addUsersToTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 2 ) );
    }

    @Test
    public void addUsersToTaskUsersAlreadyAssigned ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        User user3 = new User();
        user3.setId( 3L );
        user3.setUsername( "MockUser3" );
        inTask.getUsers().add( user3 );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.userService.userExists( "MockUser" ) ).thenReturn( true );
        when( this.userService.userExists( "MockUser2" ) ).thenReturn( true );
        when( this.userService.userExists( "MockUser3" ) ).thenReturn( true );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 3 ) );
        assertThat( dbTask.getUsers().toArray()[ 2 ], is( user3 ) );
    }

    private Task getMockTask ()
    {
        Ticket ticket = new Ticket();
        ticket.setId( 1L );

        User user = new User();
        user.setId( 1L );
        user.setUsername( "MockUser" );
        User user2 = new User();
        user2.setId( 2L );
        user2.setUsername( "MockUser2" );

        List<User> users = new ArrayList<>();
        users.add( user );
        users.add( user2 );

        Task task = new Task();
        task.setId( 1L );
        task.setTicket( ticket );
        task.setUsers( users );
        return task;
    }
}
