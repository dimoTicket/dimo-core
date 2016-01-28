package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration
@ComponentScan
public class DimoApplication
{

    public static void main ( String[] args ) throws Exception
    {
        SpringApplication.run( DimoApplication.class, args );
    }

}