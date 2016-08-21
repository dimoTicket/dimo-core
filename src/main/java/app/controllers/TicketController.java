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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class TicketController
{

    private final Log logger = LogFactory.getLog( getClass() );

    private final TicketService ticketService;

    private final ImageService imageService;

    @Autowired
    public TicketController ( ImageService imageService, TicketService ticketService )
    {
        this.imageService = imageService;
        this.ticketService = ticketService;
    }

    @RequestMapping ( value = "/api/ticket/newticket", method = RequestMethod.POST )
    public ResponseEntity submitTicket ( @Valid @RequestBody Ticket ticket )
    {
        ticket = this.ticketService.create( ticket );

        HttpHeaders httpResponseHeaders = new HttpHeaders();
        URI newTicketUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path( "/api/ticket/{id}" ).buildAndExpand( ticket.getId() ).toUri();
        httpResponseHeaders.setLocation( newTicketUri );
        return new ResponseEntity<>( httpResponseHeaders, HttpStatus.CREATED );
    }

    @RequestMapping ( value = "/api/ticket/{id}", method = RequestMethod.GET )
    public ResponseEntity getTicketByIdRest ( @PathVariable ( "id" ) Long ticketId )
    {
        Ticket ticket = this.ticketService.getById( ticketId );
        return new ResponseEntity<>( ticket, HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/tickets" )
    public ResponseEntity getTicketsRest ()
    {
        List<Ticket> tickets = this.ticketService.getAll();
        return new ResponseEntity<>( tickets, HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/ticket/getimage", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE )
    public ResponseEntity getImage ( @RequestParam ( "ticketId" ) Long ticketId,
                                     @RequestParam ( "imageName" ) String imageName )
    {
        byte[] imageContent;
        try
        {
            imageContent = FileCopyUtils.copyToByteArray( this.imageService.getImage( imageName ) );
        } catch ( IOException e )
        {
            logger.error( e.getMessage() );
            e.printStackTrace();
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.IMAGE_JPEG );
        return new ResponseEntity<>( imageContent, headers, HttpStatus.OK );
    }

    @RequestMapping ( value = "/api/ticket/getimagenames" )
    public ResponseEntity getImageNames ( @RequestParam ( "ticketId" ) Long ticketId )
    {
        this.ticketService.verifyTicketExists( ticketId );
        Collection<File> ticketImagesFiles = this.imageService.getTicketImages( ticketId );
        List<String> imageNames = ticketImagesFiles.stream()
                .map( File::getName ).collect( Collectors.toList() );
        return new ResponseEntity<>( imageNames, HttpStatus.OK );
    }

    // TODO: Generate a unique "upload token" for the client to use, to avoid malicious attempts
    @RequestMapping ( value = "/api/ticket/newimage", method = RequestMethod.POST )
    public ResponseEntity handleImageUpload ( @RequestParam ( name = "ticketId" ) Long ticketId,
                                              @RequestPart @Valid MultipartFile image )
    {
        this.imageService.saveImage( ticketId, image );
        return new ResponseEntity( HttpStatus.CREATED );
    }

}
