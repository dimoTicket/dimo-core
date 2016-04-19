package app.security;

/**
 * Holds all the Spring authorities we are gonna be using.
 * String values are the ones that will be used directly with Spring Security.
 */
public enum Authorities
{
    ADMIN( "ROLE_ADMIN" ),
    USER( "ROLE_USER" );
    private String role;

    Authorities ( String role )
    {
        this.role = role;
    }

    @Override
    public String toString ()
    {
        return this.role;
    }
}
