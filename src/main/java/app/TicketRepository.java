package app;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface TicketRepository extends CrudRepository<Ticket, Long>
{

    List<Ticket> findByMessage ( String message );
}
