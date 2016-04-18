package app.services;

import app.entities.Authority;
import app.entities.User;
import app.repositories.AuthorityRepository;
import app.repositories.UserRepository;
import app.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class UserService
{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    public User createUser ( String username, String email, String rawPassword )
    {
        User user = new User();
        user.setUsername( username );
        user.setEmail( email );
        user.setPassword( new BCryptPasswordEncoder().encode( rawPassword ) );
        user.setAuthorities( this.returnUserLevelAuthorities() );
        return this.userRepository.save( user );
    }

    private Collection<Authority> returnUserLevelAuthorities ()
    {
        ArrayList<Authority> authorities = new ArrayList<>();
        authorities.add( this.authorityRepository.findByAuthority( Authorities.USER.getSpringAuthorityRepresentation() )
                .orElseThrow( () -> new RuntimeException( "User Authority could not be retrieved from the database." ) ) );
        return authorities;
    }
}
