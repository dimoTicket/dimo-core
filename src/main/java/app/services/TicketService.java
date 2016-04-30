package app.services;

import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.exceptions.service.ResourceNotFoundException;
import app.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TicketService
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

    public List<Ticket> getAll ()
    {
        return this.ticketRepository.findAll();
    }

    public void changeStatus ( Ticket ticket, TicketStatus status )
    {
        ticket.setStatus( status );
        this.ticketRepository.save( ticket );
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
