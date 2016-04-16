package app.repositories;

import app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>
{

    /**
     * This method will be translated into a query by constructing it directly
     * from the method name as there is no other query declared.
     */
    Optional<User> findByEmail ( String email );

    /**
     * This method will be translated into a query by constructing it directly
     * from the method name as there is no other query declared.
     */
    Optional<User> findByUsername ( String username );

}
