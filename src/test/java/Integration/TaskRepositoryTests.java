package Integration;

import app.DimoApplication;
import app.entities.Task;
import app.entities.Ticket;
import app.entities.User;
import app.repositories.TaskRepository;
import app.repositories.TicketRepository;
import app.repositories.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
@Transactional
@Rollback
public class TaskRepositoryTests
{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    private Task task;

    private Ticket ticket;

    private User user;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp ()
    {
        this.ticket = new Ticket();
        ticket.setMessage( "Ticket message test" );
        ticket.setLatitude( new Double( "12.345678" ) );
        ticket.setLongitude( new Double( "12.345678" ) );
        ticket.setImageName( "imagename.jpg" );
        this.ticketRepository.save( this.ticket );

        this.user = new User();
        user.setUsername( "test" );
        user.setPassword( "testtest" );
        user.setEmail( "test@test.com" );
        user.setAuthorities( Collections.emptyList() );
        this.userRepository.save( this.user );

        this.task = new Task();
        this.task.setTicket( this.ticket );
        List<User> users = new ArrayList<>();
        users.add( this.user );
        this.task.setUsers( users );
    }

    @Test
    public void createWithEmptyUsersList ()
    {
        this.task.setUsers( Collections.emptyList() );
        this.thrown.expect( javax.validation.ConstraintViolationException.class );
        this.taskRepository.save( this.task );
    }

    @Test
    public void findByTicket ()
    {
        this.taskRepository.save( this.task );
        Optional<Task> taskOptional = this.taskRepository.findByTicket( this.ticket );
        assertThat( taskOptional.get(), is( this.task ) );
    }

    @Test
    public void findByTicketThatDoesNotExist ()
    {
        this.taskRepository.save( this.task );
        this.thrown.expect( InvalidDataAccessApiUsageException.class );
        this.taskRepository.findByTicket( new Ticket() );
    }
}
