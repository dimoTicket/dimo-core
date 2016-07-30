package integrationunit;

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

import java.util.ArrayList;

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

    @Before
    public void setUp ()
    {
        this.ticket = new Ticket();
        ticket.setMessage( "Ticket message" );
        ticket.setLatitude( new Double( "12.345678" ) );
        ticket.setLongitude( new Double( "12.345678" ) );
        ticket.setImages( new ArrayList<>() );
    }

    @Test
    public void saveTicketAndFindById ()
    {
        this.ticketRepository.save( this.ticket );
        assertThat( ticketRepository.findOne( ticket.getId() ), is( ticket ) );
    }
}
