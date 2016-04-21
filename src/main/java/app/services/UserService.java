package app.services;

import app.entities.Authority;
import app.entities.User;
import app.repositories.AuthorityRepository;
import app.repositories.UserRepository;
import app.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


@Service
public class UserService implements UserDetailsManager
{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void createUser ( UserDetails user )
    {
        User castedUser = ( User )user;
        castedUser.setAuthorities( this.returnUserLevelAuthorities() );
        try
        {
            this.userRepository.save( castedUser );
        } catch ( Exception e )
        {
            throw e;
        }
    }

    @Override
    public void updateUser ( UserDetails user )
    {
        throw new RuntimeException( "Not implemented yet." );
    }

    @Override
    public void deleteUser ( String username )
    {
        Optional<User> userOptional = this.userRepository.findByUsername( username );
        User user = userOptional.orElseThrow( () -> new UsernameNotFoundException( username ) );
        this.userRepository.delete( user );
    }

    @Override
    public void changePassword ( String oldPassword, String newPassword )
    {
        throw new RuntimeException( "Not implemented yet. Check implementation in JdbcUserDetailsManager" );
    }

    @Override
    public boolean userExists ( String username )
    {
        return this.userRepository.findByUsername( username ).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername ( String username ) throws UsernameNotFoundException
    {
        return this.userRepository.findByUsername( username ).orElseThrow( () -> new UsernameNotFoundException( username ) );
    }

    private Collection<Authority> returnUserLevelAuthorities ()
    {
        ArrayList<Authority> authorities = new ArrayList<>();
        authorities.add( this.authorityRepository.findByAuthorityString( Authorities.User )
                .orElseThrow( () -> new RuntimeException( "User Authority could not be retrieved from the database." ) ) );
        return authorities;
    }

}
