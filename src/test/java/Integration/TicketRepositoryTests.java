package Integration;

import app.DimoApplication;
import app.entities.Ticket;
import app.repositories.TicketRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
@Transactional
public class TicketRepositoryTests
{

    @Autowired
    private TicketRepository ticketRepository;
    private Ticket ticket;

    @Test
    public void test ()
    {
        ticketRepository.findAll();
    }

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
    }

    @Test
    public void saveTicketAndFindById ()
    {
        this.ticketRepository.save( this.ticket );
        assertThat( ticketRepository.findOne( ticket.getId() ), is( ticket ) );
    }
}
