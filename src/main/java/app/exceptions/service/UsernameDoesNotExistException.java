package app.exceptions.service;

public class UsernameDoesNotExistException extends UserServiceException
{

    public UsernameDoesNotExistException ( String message, Throwable cause )
    {
        super( message, cause );
    }

    public UsernameDoesNotExistException ( String message )
    {
        super( message );
    }

    public UsernameDoesNotExistException ()
    {
    }
}
