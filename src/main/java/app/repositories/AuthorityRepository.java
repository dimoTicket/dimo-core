package app.repositories;

import app.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>
{

    /**
     * This method will be translated into a query by constructing it directly
     * from the method name as there is no other query declared.
     */
    Optional<Authority> findByAuthority ( String authority );
}
