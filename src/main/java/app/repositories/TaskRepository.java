package app.repositories;

import app.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom
{

    Optional<Task> findByTicketId ( Long ticketId );
}
