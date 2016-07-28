package app.validation;

import app.entities.User;
import app.exceptions.service.UserIdDoesNotExistException;
import app.services.UserService;
import app.validation.annotations.UsersExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;


public class UsersExistValidator implements ConstraintValidator<UsersExist, Collection<User>>
{

    @Autowired
    private UserService userService;

    @Override
    public void initialize ( UsersExist constraintAnnotation )
    {
    }

    @Override
    public boolean isValid ( Collection<User> users, ConstraintValidatorContext context )
    {
        if ( users == null )
        {
            return false;
        }
        //Checks if all given user ids are present in the database
        boolean valid = false;
        valid = !users.parallelStream().anyMatch( user ->
        {
            try
            {
                this.userService.loadById( user.getId() );
                return false;
            } catch ( UserIdDoesNotExistException ex )
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate( "User with id " + user.getId() + " not found in the system" )
                        .addConstraintViolation();
                return true;
            }
        } );
        return valid;
    }

    //Used when validation gets instantiated manually during testing.
    void setUserService ( UserService userService )
    {
        this.userService = userService;
    }

}
