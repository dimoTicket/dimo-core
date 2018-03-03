package app.repositories;

import app.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom
{

    Optional<Task> findByTicketId ( Long ticketId );

    /*
    Returns: How many rows were affected
     */
    @Modifying
    @Query ( value = "delete from task_users where task_id = ?1 and  users_id = ?2",
            nativeQuery = true )
    int removeUserFromTask ( Long taskId, Long userId );
}
