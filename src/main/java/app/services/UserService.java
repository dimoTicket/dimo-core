package app.services;

import app.entities.User;
import app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService
{

    @Autowired
    private UserRepository userRepository;

    public User createUser ( String username, String email, String rawPassword )
    {
        User user = new User();
        user.setUsername( username );
        user.setEmail( email );
        user.setPassword( new BCryptPasswordEncoder().encode( rawPassword ) );
        return this.userRepository.save( user );
    }
}
