package Integration;

import app.DimoApplication;
import app.entities.Task;
import app.entities.Ticket;
import app.repositories.TaskRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
@Transactional
public class TaskRepositoryTests
{

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    private Ticket ticket;

    @Before
    public void setUp ()
    {
        this.ticket = new Ticket();
        ticket.setMessage( "Ticket message" );
        ticket.setLatitude( new Double( "12.345678" ) );
        ticket.setLongitude( new Double( "12.345678" ) );
        ticket.setImageName( "imagename.jpg" );
        Calendar calendar = Calendar.getInstance();
        calendar.set( 2000, 5, 5, 16, 16, 16 );
        ticket.setDateTime( calendar.getTime() );

        this.task.setTicket( this.ticket );
        // TODO: 26/4/2016 user
    }
}
