package app.exceptions.service;

public class UsernameAlreadyExistsException extends UserServiceException
{

    public UsernameAlreadyExistsException ( String message, Throwable cause )
    {
        super( message, cause );
    }

    public UsernameAlreadyExistsException ( String message )
    {
        super( message );
    }

    public UsernameAlreadyExistsException ()
    {
    }
}
