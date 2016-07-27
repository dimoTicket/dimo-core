package app.validation;

import app.entities.Ticket;
import app.exceptions.service.ResourceNotFoundException;
import app.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class TicketExistsValidator implements ConstraintValidator<TicketExists, Ticket>
{

    @Autowired
    private TicketService ticketService;

    @Override
    public void initialize ( TicketExists constraintAnnotation )
    {
    }

    @Override
    public boolean isValid ( Ticket ticket, ConstraintValidatorContext context )
    {
        try
        {
            this.ticketService.verifyTicketExists( ticket.getId() );
            return true;
        } catch ( ResourceNotFoundException e )
        {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate( "Ticket with id " + ticket.getId() + " not found in the system" )
                    .addConstraintViolation();
            return false;
        }
    }

    //Used when validation gets instantiated manually during testing.
    void setTicketService ( TicketService ticketService )
    {
        this.ticketService = ticketService;
    }
}
