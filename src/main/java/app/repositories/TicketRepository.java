package app.repositories;

import app.entities.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface TicketRepository extends CrudRepository<Ticket, Long>
{

    List<Ticket> findByMessage ( String message );
}
