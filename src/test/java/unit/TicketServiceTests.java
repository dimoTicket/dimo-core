package unit;

import app.DimoApplication;
import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.exceptions.service.ResourceNotFoundException;
import app.repositories.TicketRepository;
import app.services.TicketService;
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

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
public class TicketServiceTests
{

    @Autowired
    @InjectMocks
    private TicketService ticketService;

    @Mock
    TicketRepository ticketRepository;

    private Ticket ticket;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );

        ticket = new Ticket();
        ticket.setId( 1L );
        ticket.setMessage( "Ticket message 1" );
        ticket.setImages( new ArrayList<>() );
        ticket.setLatitude( 12.345678 );
        ticket.setLongitude( 25.579135 );
        ticket.setStatus( TicketStatus.NEW );
    }

    @Test
    public void changeStatus ()
    {
        when( this.ticketRepository.save( ticket ) ).thenReturn( ticket );
        when( this.ticketRepository.findOne( ticket.getId() ) ).thenReturn( ticket );
        ticket = this.ticketService.changeStatus( ticket, TicketStatus.IN_PROGRESS );
        assertThat( ticket.getStatus(), is( TicketStatus.IN_PROGRESS ) );
    }

    @Test
    public void getById ()
    {
        when( this.ticketRepository.findOne( this.ticket.getId() ) ).thenReturn( this.ticket );
        Ticket foundTicket = this.ticketService.getById( this.ticket.getId() );
        assertThat( foundTicket, is( this.ticket ) );
    }

    @Test
    public void getByIdForTicketThatDoesNotExist ()
    {
        when( this.ticketRepository.findOne( this.ticket.getId() ) ).thenReturn( null );
        this.thrown.expect( ResourceNotFoundException.class );
        this.ticketService.getById( this.ticket.getId() );
    }

    @Test
    public void verifyTicketExists ()
    {
        when( this.ticketRepository.findOne( this.ticket.getId() ) ).thenReturn( this.ticket );
        this.ticketService.verifyTicketExists( this.ticket.getId() );
    }

    @Test
    public void verifyTicketExistsForMissingTicket ()
    {
        when( this.ticketRepository.findOne( this.ticket.getId() ) ).thenReturn( null );
        this.thrown.expect( ResourceNotFoundException.class );
        this.ticketService.verifyTicketExists( this.ticket.getId() );
    }
}
