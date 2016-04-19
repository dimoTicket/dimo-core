package app.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Holds all the Spring authorities we are gonna be using.
 * String values are the ones that will be used directly with Spring Security.
 */
public class Authorities
{

    public static String Admin = "ROLE_ADMIN";
    public static String User = "ROLE_USER";

    public static Collection<String> getAllAuthorities ()
    {
        return Arrays.stream( Authorities.class.getDeclaredFields() ).map( String.class::cast ).collect( Collectors.toList() );
    }
}