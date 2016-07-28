package app.validation.annotations;

import app.validation.TicketExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
@Constraint ( validatedBy = TicketExistsValidator.class )
@Documented
public @interface TicketExists
{

    String message () default "TicketExists validation error - default message";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}
