package app.controllers;

import app.entities.Ticket;
import app.exceptions.service.ResourceNotFoundException;
import app.repositories.TicketRepository;
import app.services.ImageService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;


@Controller
public class TicketController
{

    private final Log logger = LogFactory.getLog( getClass() );

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ImageService imageService;

    @RequestMapping ( value = "/ticket/{id}", method = RequestMethod.GET )
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

    @RequestMapping ( value = "/api/ticket/{id}", method = RequestMethod.GET )
    public ResponseEntity getTicketMessageByIdRest ( @PathVariable ( "id" ) Long id )
    {
        this.verifyTicketExists( id );
        Ticket ticket = this.ticketRepository.findOne( id );
        return new ResponseEntity<>( ticket, HttpStatus.OK );
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
    public String getAllTickets ( Map model )
    {
        List<Ticket> tickets = this.ticketRepository.findAll();
        if ( !tickets.isEmpty() )
        {
            model.put( "tickets", tickets );
            return "tickets";
        }
        return "error";
    }

    @RequestMapping ( value = "/api/ticket/newticket", method = RequestMethod.POST )
    public ResponseEntity submitTicket ( @Valid @RequestBody Ticket ticket )
    {
        ticket = this.ticketRepository.save( ticket );
        HttpHeaders httpResponseHeaders = new HttpHeaders();
        URI newTicketUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path( "/api/{id}" ).buildAndExpand( ticket.getId() ).toUri();
        httpResponseHeaders.setLocation( newTicketUri );
        return new ResponseEntity<>( httpResponseHeaders, HttpStatus.CREATED );
    }

    // TODO: 10/02/2016 Generate a unique "upload token" for the client to use, to avoid malicious attempts
    @RequestMapping ( value = "/api/ticket/newticketimage", method = RequestMethod.POST )
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

    protected void verifyTicketExists ( Long ticketId ) throws ResourceNotFoundException
    {
        Ticket ticket = this.ticketRepository.findOne( ticketId );
        if ( ticket == null )
        {
            throw new ResourceNotFoundException( "Ticket with id " + ticketId + " not found" );
        }
    }
}
