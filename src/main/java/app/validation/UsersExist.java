package app.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
@Constraint ( validatedBy = UsersExistValidator.class )
@Documented
public @interface UsersExist
{

    String message () default "UsersExist validation error - default message";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}
