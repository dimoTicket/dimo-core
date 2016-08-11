package app.controllers;

import app.entities.User;
import app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@Controller
public class UserController
{

    private final UserService userService;

    @Autowired
    public UserController ( UserService userService )
    {
        this.userService = userService;
    }

    @RequestMapping ( value = "/api/register", method = RequestMethod.POST )
    public ResponseEntity registerUser ( @Valid @RequestBody User user )
    {
        this.userService.createUser( user );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }

    @RequestMapping ( value = "/api/users" )
    public ResponseEntity getAllUsers ()
    {
        return new ResponseEntity<>( this.userService.getAllUsers(), HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/user/{id}", method = RequestMethod.GET )
    public ResponseEntity getUserByIdRest ( @PathVariable ( "id" ) Long userId )
    {
        User user = this.userService.loadById( userId );
        return new ResponseEntity<>( user, HttpStatus.OK );
    }
}
