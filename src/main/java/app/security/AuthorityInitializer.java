package app.security;

import app.entities.Authority;
import app.repositories.AuthorityRepository;
import app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;


@Component
public class AuthorityInitializer
{

    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserService userService;

    @PostConstruct
    private void initialize ()
    {
        this.initializeSecurityAuthorities();
        this.seedSampleUser();
    }

    /**
     * Checks if Authorities are already in the database(due to consecutive application executions while preserving the schema)
     * and persists them if they aren't.
     */
    private void initializeSecurityAuthorities ()
    {
        Arrays.stream( Authorities.values() )
                .filter( auth -> !this.authorityRepository.findByAuthority( auth.toString() ).isPresent() )
                .forEach( auth -> this.authorityRepository.save( new Authority( auth.toString() ) ) );
    }

    private void seedSampleUser ()
    {
        this.userService.createUser( "pambos", "pambos@pambos.gr", "12345678" );
    }
}
