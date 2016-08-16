package app.exceptions.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus ( HttpStatus.CONFLICT )
public class ImageAlreadyExistsException extends RuntimeException
{

    public ImageAlreadyExistsException ()
    {
        super();
    }

    public ImageAlreadyExistsException ( String message )
    {
        super( message );
    }

    public ImageAlreadyExistsException ( String message, Throwable cause )
    {
        super( message, cause );
    }
}
