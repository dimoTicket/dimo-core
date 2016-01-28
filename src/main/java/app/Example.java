package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@EnableAutoConfiguration
@ComponentScan
public class Example
{
    private static final Logger log = LoggerFactory.getLogger(Example.class);

    @Autowired
    private TicketRepository ticketRepository;

    @RequestMapping ( "/" )
    String home ()
    {
        return "Hello World!";
    }

    @RequestMapping ( "/lol" )
    String wtf ()
    {
        ticketRepository.deleteAll();
        ticketRepository.save( new Ticket( "pambos" ) );
        ticketRepository.save( new Ticket( "antreas" ) );
        log.info( ticketRepository.findByMessage( "pambos" ).get( 0 ).toString() );
        return "omg lol -> " + LocalDateTime.now();
    }

    public static void main ( String[] args ) throws Exception
    {
        SpringApplication.run( Example.class, args );
    }

}