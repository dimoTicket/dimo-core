package unit;

import app.DimoApplication;
import app.entities.Task;
import app.entities.Ticket;
import app.exceptions.service.ResourceNotFoundException;
import app.repositories.TaskRepository;
import app.services.TaskService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
public class TaskServiceTests
{

    @Autowired
    @InjectMocks
    private TaskService taskService;

    @Mock
    TaskRepository taskRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );
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
}
