package app.security;

import app.entities.Authority;
import app.entities.User;
import app.repositories.AuthorityRepository;
import app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


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
        Authorities.getAllAuthorities().stream()
                .filter( auth -> !this.authorityRepository.findByAuthorityString( auth ).isPresent() )
                .forEach( auth -> this.authorityRepository.save( new Authority( auth ) ) );
    }

    private void seedSampleUser ()
    {
        User user = new User();
        user.setUsername( "pambos" );
        user.setPassword( "12345678" );
        user.setEmail( "pambos@pambos.gr" );
        this.userService.createUser( user );
    }
}
