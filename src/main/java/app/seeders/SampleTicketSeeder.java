package app.seeders;

import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
@Profile ( "dev" )
public class SampleTicketSeeder implements ApplicationListener<ContextRefreshedEvent>
{

    private final TicketService ticketService;

    @Autowired
    public SampleTicketSeeder ( TicketService ticketService )
    {
        this.ticketService = ticketService;
    }

    @Override
    public void onApplicationEvent ( ContextRefreshedEvent event )
    {
        this.seedSampleTickets();
    }

    private void seedSampleTickets ()
    {
        Ticket ticket1 = new Ticket();
        ticket1.setLatitude( 40.631756 );
        ticket1.setLongitude( 22.951907 );
        ticket1.setMessage( "Ticket message 1" );
        this.ticketService.create( ticket1 );

        Ticket ticket2 = new Ticket();
        ticket2.setLatitude( 40.631756 );
        ticket2.setLongitude( 22.951907 );
        ticket2.setMessage( "Ticket message 2" );
        ticket2.setStatus( TicketStatus.REJECTED );
        this.ticketService.create( ticket2 );

        Ticket ticket3 = new Ticket();
        ticket3.setLatitude( 40.631756 );
        ticket3.setLongitude( 22.951907 );
        ticket3.setMessage( "Ticket message 3" );
        ticket3.setStatus( TicketStatus.ASSIGNED );
        this.ticketService.create( ticket3 );
    }
}
