package app.services;

import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.repositories.TicketRepository;
import org.springframework.stereotype.Service;


@Service
public class TicketService
{

    private TicketRepository ticketRepository;

    public Ticket changeTicketStatus ( Ticket ticket, TicketStatus status )
    {
        ticket.setStatus( status );
        return this.ticketRepository.save( ticket );
    }
}
