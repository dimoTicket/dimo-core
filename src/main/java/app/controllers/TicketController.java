package app.controllers;

import app.entities.Ticket;
import app.repositories.TicketRepository;
import app.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping ( "/" )
public class TicketController
{

    private static final Logger logger = LoggerFactory.getLogger( TicketController.class );
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ImageService imageService;

    @RequestMapping ( value = "/{id}", method = RequestMethod.GET )
    public String getTicketMessageById ( @PathVariable ( "id" ) Long id, Model model )
    {
        Ticket ticket = this.ticketRepository.findOne( id );
        if ( ticket != null )
        {
            model.addAttribute( "ticket", ticket );
            return "ticket";
        }
        return "error"; //// TODO: 09/02/2016 should return empty model. Frotnend should take care of showing the msg
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
    public ResponseEntity<Ticket> submitTicketRest ( @RequestBody @Valid Ticket ticket )
    {
        return new ResponseEntity<>( this.ticketRepository.save( ticket ), HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/newticketimage", method = RequestMethod.POST )
    public ResponseEntity handleImageUpload ( @RequestParam ( name = "ticketId", required = false ) Long ticketId,
                                              @RequestPart @Valid MultipartFile multipartFile )
    {
        this.imageService.saveMultipartFile( multipartFile );
        return new ResponseEntity( HttpStatus.CREATED );
    }
}
