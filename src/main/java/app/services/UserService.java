package app.services;

import app.entities.Authority;
import app.entities.User;
import app.exceptions.service.EmailAlreadyInUseException;
import app.exceptions.service.UserServiceException;
import app.exceptions.service.UsernameAlreadyExistsException;
import app.exceptions.service.UsernameDoesNotExistException;
import app.repositories.AuthorityRepository;
import app.repositories.UserRepository;
import app.security.Authorities;
import app.security.SecurityConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


@Service
public class UserService implements UserDetailsManager
{

    private final Log logger = LogFactory.getLog( getClass() );
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void createUser ( UserDetails user )
    {
        User castedUser = ( User )user;
        this.verifyUser( castedUser );

        castedUser.setAuthorities( this.returnUserLevelAuthorities() );
        castedUser.setPassword( SecurityConfiguration.passwordEncoder.encode( castedUser.getPassword() ) );
        try
        {
            this.userRepository.save( castedUser );
        } catch ( Exception e )
        {
            logger.error( e.getMessage() );
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
        User user = userOptional.orElseThrow( () -> new UsernameDoesNotExistException( "Username " + username + " does not exist" ) );
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

    public boolean emailExists ( String email )
    {
        return this.userRepository.findByEmail( email ).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername ( String username )
    {
        return this.userRepository.findByUsername( username )
                .orElseThrow( () -> new UsernameDoesNotExistException( "Username " + username + " does not exist" ) );
    }

    private Collection<Authority> returnUserLevelAuthorities ()
    {
        ArrayList<Authority> authorities = new ArrayList<>();
        authorities.add( this.authorityRepository.findByAuthorityString( Authorities.User )
                .orElseThrow( () -> new RuntimeException( "User Authority could not be retrieved from the database." ) ) );
        return authorities;
    }

    private void verifyUser ( User user ) throws UserServiceException
    {
        if ( this.userExists( user.getUsername() ) )
        {
            throw new UsernameAlreadyExistsException( "Username " + user.getUsername() + " already exists" );
        }
        if ( this.emailExists( user.getEmail() ) )
        {
            throw new EmailAlreadyInUseException( "Email " + user.getEmail() + " already exists" );
        }
    }

}
