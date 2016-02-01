package app.controllers;

import app.entities.Ticket;
import app.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping ( "/" )
public class TicketController
{

    private static final Logger logger = LoggerFactory.getLogger( TicketController.class );
    @Autowired
    private TicketRepository ticketRepository;

    @RequestMapping ( value = "/{id}", method = RequestMethod.GET )
    public String getTicketMessageById ( @PathVariable ( "id" ) Long id, Model model )
    {
        Ticket ticket = this.ticketRepository.findOne( id );
        if ( ticket != null )
        {
            model.addAttribute( "ticket", ticket );
            return "ticket";
        }
        return "error";
    }

    @RequestMapping ( value = "/tickets", method = RequestMethod.GET )
    public String getAllTickets ( Model model )
    {
        List<Ticket> tickets = this.ticketRepository.findAll();
        if ( !tickets.isEmpty() )
        {
            model.addAttribute( "tickets", tickets );
            return "tickets";
        }
        return "error";
    }

    @RequestMapping ( value = "/newticket", method = RequestMethod.GET )
    public String showTicketForm ( Model model )
    {
        model.addAttribute( "ticket", new Ticket() );
        return "newticketform";
    }

    @RequestMapping ( value = "/newticket", method = RequestMethod.POST )
    public String submitTicketForm ( @ModelAttribute Ticket ticket )
    {
        this.ticketRepository.save( ticket );
        return "newticketsuccess";
    }

    @RequestMapping ( value = "/api/newticket", method = RequestMethod.POST )
    public ResponseEntity<Ticket> submitTicketRest ( @RequestBody Ticket ticket )
    {
        return new ResponseEntity<>( this.ticketRepository.save( ticket ), HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/newticketimage", method = RequestMethod.POST )
    public ResponseEntity handleImageUpload ( @RequestParam ( name = "ticketId", required = false ) Long ticketId,
                                              @RequestPart MultipartFile multipartFile )
    {
        File convFile = new File( //XXX);
        try
        {
            multipartFile.transferTo( convFile );
            logger.info( "path" + convFile.getAbsolutePath() );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }

        return new ResponseEntity( HttpStatus.CREATED );
    }
}

