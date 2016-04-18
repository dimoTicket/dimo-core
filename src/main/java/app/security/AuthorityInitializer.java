package app.security;

import app.entities.Authority;
import app.repositories.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;


/**
 * Seeds the database with the authorities we are gonna be using, defined in {@link Authorities}
 */
@Component
public class AuthorityInitializer
{

    @Autowired
    private AuthorityRepository authorityRepository;

    @PostConstruct
    private void initialize ()
    {
        this.initializeSecurityAuthorities();
    }

    /**
     * Checks if Authorities are already in the database(due to consecutive application executions while preserving the schema)
     * and persists them if they aren't.
     */
    private void initializeSecurityAuthorities ()
    {
        Arrays.stream( Authorities.values() )
                .filter( auth -> !this.authorityRepository.findByAuthority( auth.getSpringAuthorityRepresentation() ).isPresent() )
                .forEach( auth -> this.authorityRepository.save( new Authority( auth.getSpringAuthorityRepresentation() ) ) );
    }
}
