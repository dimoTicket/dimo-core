package app.validation;

import app.services.Service;
import app.services.TicketService;
import app.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintValidator;
import java.util.List;


public class TestConstraintValidationFactory extends SpringWebConstraintValidatorFactory
{

    private final Log logger = LogFactory.getLog( getClass() );

    private final WebApplicationContext wac;

    private List<app.services.Service> services;

    public TestConstraintValidationFactory ( WebApplicationContext wac, List<Service> services )
    {
        this.wac = wac;
        this.services = services;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance ( Class<T> key )
    {
        logger.info( "key is : " + key );
        ConstraintValidator instance = super.getInstance( key );

        if ( instance instanceof TicketExistsValidator )
        {
            TicketExistsValidator ticketExistsValidator = ( TicketExistsValidator )instance;
            ticketExistsValidator.setTicketService( services.stream()
                    .filter( service -> service instanceof TicketService )
                    .map( TicketService.class::cast )
                    .findFirst().orElseThrow( () -> new IllegalArgumentException( "TicketService not found in passed services list" ) ) );
            instance = ticketExistsValidator;
        } else if ( instance instanceof UsersExistValidator )
        {
            UsersExistValidator usersExistValidator = ( UsersExistValidator )instance;
            usersExistValidator.setUserService( services.stream()
                    .filter( service -> service instanceof UserService )
                    .map( UserService.class::cast )
                    .findFirst().orElseThrow( () -> new IllegalArgumentException( "UserService not found in passed services list" ) ) );
            instance = usersExistValidator;
        }
        return ( T )instance;
    }

    @Override
    protected WebApplicationContext getWebApplicationContext ()
    {
        return wac;
    }

}