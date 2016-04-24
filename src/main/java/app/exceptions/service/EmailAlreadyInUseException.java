package app.exceptions.service;

public class EmailAlreadyInUseException extends UserServiceException
{

    public EmailAlreadyInUseException ( String message, Throwable cause )
    {
        super( message, cause );
    }

    public EmailAlreadyInUseException ( String message )
    {
        super( message );
    }

    public EmailAlreadyInUseException ()
    {
    }
}
