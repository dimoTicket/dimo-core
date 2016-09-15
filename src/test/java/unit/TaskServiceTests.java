package unit;

import app.DimoApplication;
import app.entities.Task;
import app.entities.Ticket;
import app.entities.User;
import app.entities.enums.TicketStatus;
import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.service.UserIdDoesNotExistException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.*;


@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = DimoApplication.class )
@ActiveProfiles ( "unit-tests" )
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

        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.empty() );
        Answer<Task> saveAnswer = invocation ->
        {
            task.setId( 1L );
            return task;
        };

        when( this.taskRepository.saveFlushAndRefresh( any( Task.class ) ) ).thenAnswer( saveAnswer );

        when( this.userService.userExists( 1L ) ).thenReturn( true );
        when( this.userService.userExists( 2L ) ).thenReturn( true );

        Task returnTask = this.taskService.create( task );
        verify( ticketService ).changeStatus( returnTask.getTicket().getId(), TicketStatus.ASSIGNED );
    }

    @Test
    public void createForTicketThatDoesNotExist ()
    {
        Task task = this.getMockTask();
        doThrow( new ResourceNotFoundException() ).when( this.ticketService ).verifyTicketExists( 1L );

        this.thrown.expect( ResourceNotFoundException.class );
        this.taskService.create( task );
    }

    @Test
    public void createForTicketThatAlreadyHasATask ()
    {
        Task task = this.getMockTask();
        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.of( new Task() ) );
        when( this.userService.userExists( any( String.class ) ) ).thenReturn( true );

        this.thrown.expect( BadRequestException.class );
        this.taskService.create( task );
    }

    @Test
    public void createWhenAllUsersDontExist ()
    {
        Task task = this.getMockTask();
        doNothing().when( ticketService ).verifyTicketExists( 1L );
        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.empty() );
        when( this.userService.userExists( any( Long.class ) ) ).thenReturn( false );

        this.thrown.expect( UserIdDoesNotExistException.class );
        this.taskService.create( task );
    }

    @Test
    public void createWhenOneUserDoesNotExist ()
    {
        Task task = this.getMockTask();
        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.empty() );
        when( this.userService.userExists( 1L ) ).thenReturn( false );
        when( this.userService.userExists( 2L ) ).thenReturn( true );

        this.thrown.expect( UserIdDoesNotExistException.class );
        this.taskService.create( task );
    }

    @Test
    public void getTaskForTicket ()
    {
        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.of( new Task() ) );
        Task taskForTicket = this.taskService.getTaskByTicketId( 1L );
        assertThat( taskForTicket, is( not( nullValue() ) ) );
    }

    @Test
    public void getTaskForTicketThatDoesNotExist ()
    {
        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.empty() );
        this.thrown.expect( ResourceNotFoundException.class );
        this.taskService.getTaskByTicketId( 404L );
    }

    @Test
    public void taskExistsForTicket ()
    {
        when( this.taskRepository.findByTicketId( any( ) ) ).thenReturn( Optional.of( new Task() ) );
        assertThat( this.taskService.taskExistsForTicketId( 1L ), is( true ) );
    }

    @Test
    public void taskExistsForTicketThatDoesNotExist ()
    {
        when( this.taskRepository.findByTicketId( any() ) ).thenReturn( Optional.empty() );
        assertThat( this.taskService.taskExistsForTicketId( 404L ), is( false ) );
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
        when( this.userService.userExists( 3L ) ).thenReturn( true );
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
        when( this.userService.userExists( 3L ) ).thenReturn( true );
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
        assertThat( dbTask.getUsers(), hasSize( dbTask.getUsers().size() ) );
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
        when( this.userService.loadById( 3L ) ).thenThrow( new UserIdDoesNotExistException( "" ) );
        thrown.expect( UserIdDoesNotExistException.class );
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
        when( this.userService.loadById( 3L ) ).thenReturn( user3 );
        when( this.userService.loadById( 3L ) ).thenThrow( new UserIdDoesNotExistException() );
        thrown.expect( UserIdDoesNotExistException.class );
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
        when( this.userService.userExists( 1L ) ).thenReturn( true );
        when( this.userService.userExists( 2L ) ).thenReturn( true );
        when( this.userService.userExists( 3L ) ).thenReturn( true );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.addUsersToTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 3 ) );
        assertThat( dbTask.getUsers().toArray()[ 2 ], is( user3 ) );
    }

    @Test
    public void removeUsersFromTask ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.setUsers( inTask.getUsers().stream().limit( 1 ).collect( Collectors.toList() ) );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.removeUsersFromTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 1 ) );
    }

    //// FIXME: 13/7/2016 Looks like javax validation doesn't work for this one. Investigate
    @Test
    public void removeUsersFromTaskRemoveAllUsers ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.removeUsersFromTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( 0 ) );
    }

    @Test
    public void removeUsersFromTaskEmptyInUserList ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.removeUsersFromTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( dbTask.getUsers().size() ) );
    }

    @Test
    public void removeUsersFromTaskInvalidUser ()
    {
        Task dbTask = this.getMockTask();
        Task inTask = this.getMockTask();
        inTask.getUsers().clear();
        User invalidUser = new User();
        invalidUser.setId( 999L );
        invalidUser.setUsername( "InvalidUser" );
        inTask.getUsers().add( invalidUser );

        when( this.taskRepository.findOne( 1L ) ).thenReturn( dbTask );
        when( this.taskRepository.save( dbTask ) ).thenReturn( dbTask );
        dbTask = this.taskService.removeUsersFromTask( inTask );
        assertThat( dbTask.getUsers(), hasSize( dbTask.getUsers().size() ) );
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
