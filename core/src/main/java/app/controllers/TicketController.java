package app.controllers;

import app.entities.Ticket;
import app.services.ImageService;
import app.services.TicketService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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


@Controller
public class TicketController
{

    private final Log logger = LogFactory.getLog( getClass() );

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ImageService imageService;


    @RequestMapping ( value = "/api/ticket/{id}", method = RequestMethod.GET )
    public ResponseEntity getTicketByIdRest ( @PathVariable ( "id" ) Long ticketId )
    {
        this.ticketService.verifyTicketExists( ticketId );
        Ticket ticket = this.ticketService.getById( ticketId );
        return new ResponseEntity<>( ticket, HttpStatus.OK );
    }

    @RequestMapping ( value = "/image", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getImage ( @RequestParam ( "ticketId" ) Long ticketId )
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
            logger.error( "Picture not found. Ticket ticketId is : " + ticketId );
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.IMAGE_JPEG );
        return new ResponseEntity<>( imageContent, headers, HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/tickets" )
    public ResponseEntity getTicketsRest ()
    {
        List<Ticket> tickets = this.ticketService.getAll();
        return new ResponseEntity<>( tickets, HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/ticket/newticket", method = RequestMethod.POST )
    public ResponseEntity submitTicket ( @Valid @RequestBody Ticket ticket )
    {
        ticket = this.ticketService.create( ticket );
        HttpHeaders httpResponseHeaders = new HttpHeaders();
        URI newTicketUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path( "/api/ticket/{ticketId}" ).buildAndExpand( ticket.getId() ).toUri();
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

}
