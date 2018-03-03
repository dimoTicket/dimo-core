package app.repositories;

import app.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom
{

    Optional<Task> findByTicketId ( Long ticketId );

    /**
     * Used to solve a phantom read problem where hibernate would skip parameter "users_id" in the generated query,
     * leading to deletion of all other users in the table. (This occurs when a burst of mixed requests is received)
     * https://github.com/dimoTicket/dimo-core/issues/26
     *
     * @param taskId The task id from which a user will be deleted.
     * @param userId user id to be deleted from the task.
     * @return How many rows were affected
     */

    //todo: Write tests for this method since dialect fuckup is possible when switching between mysql and h2
    @Modifying
    @Query ( value = "delete from task_users where task_id = ?1 and  users_id = ?2",
            nativeQuery = true )
    int removeUserFromTask ( Long taskId, Long userId );
}
