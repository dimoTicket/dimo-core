package app.repositories;

import app.entities.Task;
import app.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom
{

    Optional<Task> findByTicket ( Ticket ticket );
}
