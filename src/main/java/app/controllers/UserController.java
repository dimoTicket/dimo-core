package app.controllers;

import app.entities.User;
import app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@Controller
public class UserController
{

    @Autowired
    private UserService userService;


    @RequestMapping ( value = "/register", method = RequestMethod.POST )
    public ResponseEntity registerUser ( @Valid @ModelAttribute User user )
    {
        this.userService.createUser( user );
        return new ResponseEntity( HttpStatus.CREATED );
    }

    @RequestMapping ( value = "/users" )
    public ResponseEntity getAllUsers ()
    {
        return new ResponseEntity<>( this.userService.getAllUsers(), HttpStatus.OK );
    }
}
