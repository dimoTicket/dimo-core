package app.services;

import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.exceptions.service.ResourceNotFoundException;
import app.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TicketService implements app.services.Service
{

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket getById ( Long ticketId )
    {
        this.verifyTicketExists( ticketId );
        return this.ticketRepository.findOne( ticketId );
    }

    public Ticket create ( Ticket ticket )
    {
        return this.ticketRepository.save( ticket );
    }

    public Ticket update ( Ticket ticket )
    {
        return this.ticketRepository.saveAndFlush( ticket );
    }

    public List<Ticket> getAll ()
    {
        return this.ticketRepository.findAll();
    }

    public Ticket changeStatus ( Ticket ticket, TicketStatus status )
    {
        this.verifyTicketExists( ticket.getId() );
        ticket.setStatus( status );
        return this.ticketRepository.save( ticket );
    }

    public void verifyTicketExists ( Long ticketId ) throws ResourceNotFoundException
    {
        Ticket ticket = this.ticketRepository.findOne( ticketId );
        if ( ticket == null )
        {
            throw new ResourceNotFoundException( "Ticket with id " + ticketId + " not found" );
        }
    }
}
