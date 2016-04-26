package app.services;

import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TicketService
{

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> getAllTickets ()
    {
        return this.ticketRepository.findAll();
    }

    public Ticket changeTicketStatus ( Ticket ticket, TicketStatus status )
    {
        ticket.setStatus( status );
        return this.ticketRepository.save( ticket );
    }
}
