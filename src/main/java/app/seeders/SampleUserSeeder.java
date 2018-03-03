package app.seeders;

import app.entities.User;
import app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
@Profile ( "dev" )
public class SampleUserSeeder implements ApplicationListener<ContextRefreshedEvent>
{

    private final UserService userService;

    @Autowired
    public SampleUserSeeder ( UserService userService )
    {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent ( ContextRefreshedEvent event )
    {
        this.seedSampleUsers();
    }

    private void seedSampleUsers ()
    {
        User user = new User();
        user.setUsername( "User1" );
        user.setPassword( "12345678" );
        user.setEmail( "user1@dimo.com" );
        this.userService.createUser( user );

        User user2 = new User();
        user2.setUsername( "User2" );
        user2.setPassword( "12345678" );
        user2.setEmail( "user2@dimo.com" );
        this.userService.createUser( user2 );

        User user3 = new User();
        user3.setUsername( "User3" );
        user3.setPassword( "12345678" );
        user3.setEmail( "user3@dimo.com" );
        this.userService.createUser( user3 );

        User user4 = new User();
        user4.setUsername( "User4" );
        user4.setPassword( "12345678" );
        user4.setEmail( "user4@dimo.com" );
        this.userService.createUser( user4 );
    }
}
