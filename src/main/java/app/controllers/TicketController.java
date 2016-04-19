package app.controllers;

import app.entities.Ticket;
import app.repositories.TicketRepository;
import app.services.ImageService;
import app.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Controller
public class TicketController
{

    private final Log logger = LogFactory.getLog( getClass() );
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserService userService;

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

    @RequestMapping ( value = "/image", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getImage ( @RequestParam ( "id" ) Long ticketId )
    {
        byte[] imageContent = null;
        try
        {
            InputStream inputStream = new FileInputStream( this.imageService.findPictureFileOfTicketId( ticketId ) );
            BufferedImage img = ImageIO.read( inputStream );
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write( img, "jpg", bao );
            imageContent = bao.toByteArray();
        } catch ( IOException e )
        {
            logger.error( "Picture not found. Ticket id is : " + ticketId );
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.IMAGE_JPEG );
        return new ResponseEntity<>( imageContent, headers, HttpStatus.OK );
    }

    @RequestMapping ( value = { "/tickets", "/" }, method = RequestMethod.GET )
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
    public ResponseEntity createDummyTicket ()
    {
        Ticket ticket = new Ticket();
        ticket.setMessage( "Ticket message" );
        ticket.setLatitude( new Double( "12.345678" ) );
        ticket.setLongitude( new Double( "12.345678" ) );
        ticket.setImageName( "imagename.jpg" );

        return new ResponseEntity<>( this.ticketRepository.save( ticket ), HttpStatus.OK );
    }
//
//    @RequestMapping ( value = "/newticket", method = RequestMethod.POST )
//    public String submitTicketForm ( @ModelAttribute Ticket ticket )
//    {
//        this.ticketRepository.save( ticket );
//        return "newticketsuccess";
//    }

    @RequestMapping ( value = "/api/newticket", method = RequestMethod.POST )
    public ResponseEntity<Ticket> submitTicketRest ( @RequestBody @Valid Ticket ticket )
    {
        return new ResponseEntity<>( this.ticketRepository.save( ticket ), HttpStatus.OK );
    }

    // TODO: 10/02/2016 Generate a unique "upload token" for the client to use, to avoid malicious attempts
    @RequestMapping ( value = "/api/newticketimage", method = RequestMethod.POST )
    public ResponseEntity handleImageUpload ( @RequestParam ( name = "ticketId", required = true ) Long ticketId,
                                              @RequestPart @Valid MultipartFile multipartFile )
    {
        try
        {
            this.imageService.saveMultipartFile( ticketId, multipartFile );
            return new ResponseEntity( HttpStatus.CREATED );
        } catch ( IllegalArgumentException e )
        {
            logger.error( "Attempt to overwrite picture for ticketId : " + ticketId );
        }
        return new ResponseEntity( HttpStatus.BAD_REQUEST );
    }
}
