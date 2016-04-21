package app.controllers;

import app.entities.User;
import app.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@Controller
public class UserController
{

    private final Log logger = LogFactory.getLog( getClass() );
    @Autowired
    private UserService userService;

    @RequestMapping ( value = "/register" )
    public String showRegistrationForm ( Map model )
    {
        model.put( "user", new User() );
        return "userregistrationform";
    }

    @RequestMapping ( value = "/register", method = RequestMethod.POST )
    public ResponseEntity registerUser ( @ModelAttribute User user )
    {
        try
        {
            this.userService.createUser( user );
            return new ResponseEntity( HttpStatus.OK );
        } catch ( Exception e )
        {
            return new ResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
}
