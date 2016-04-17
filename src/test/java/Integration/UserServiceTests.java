package Integration;

import app.DimoApplication;
import app.entities.User;
import app.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
@Transactional
public class UserServiceTests
{

    @Autowired
    private UserService userService;

    @Test
    public void createUser ()
    {
        String rawPassword = "TestRawPassword";
        User fetchedUser = this.userService.createUser( "TestUsername", "Test@JunitTest.gr", rawPassword );
        assertNotNull( fetchedUser.getId() );
        //Password encryption check
        assertTrue( new BCryptPasswordEncoder().matches( rawPassword, fetchedUser.getPassword() ) );
    }
}
