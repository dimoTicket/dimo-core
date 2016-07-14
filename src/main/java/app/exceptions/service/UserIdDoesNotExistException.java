package app.exceptions.service;

public class UserIdDoesNotExistException extends UserServiceException
{

    public UserIdDoesNotExistException ( String message, Throwable cause )
    {
        super( message, cause );
    }

    public UserIdDoesNotExistException ( String message )
    {
        super( message );
    }

    public UserIdDoesNotExistException ()
    {
    }
}
