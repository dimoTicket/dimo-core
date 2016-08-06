package springmvc;

import app.controllers.TicketController;
import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.exceptions.service.ResourceNotFoundException;
import app.services.ImageService;
import app.services.TicketService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith ( MockitoJUnitRunner.class )
public class TicketControllerTests
{

    @Mock
    ImageService imageService;

    @Mock
    TicketService ticketService;

    private MockMvc mockMvc;

    private Ticket ticket;

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );
        mockMvc = standaloneSetup( new TicketController( imageService, ticketService ) )
                .build();

        ticket = new Ticket();
        ticket.setId( 1L );
        ticket.setMessage( "Ticket message 1" );
        ticket.setImages( new ArrayList<>() );
        ticket.setLatitude( 12.345678 );
        ticket.setLongitude( 25.579135 );
        ticket.setStatus( TicketStatus.NEW );
    }

    @Test
    public void getTicketByIdRest () throws Exception
    {
        when( ticketService.getById( this.ticket.getId() ) ).thenReturn( this.ticket );
        mockMvc.perform( get( "/api/ticket/" + this.ticket.getId() ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( ( jsonPath( "id" ).value( this.ticket.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "message" ).value( this.ticket.getMessage() ) ) )
                .andExpect( ( jsonPath( "images" ).isArray() ) )
                .andExpect( ( jsonPath( "images" ).isEmpty() ) )
                .andExpect( ( jsonPath( "latitude" ).value( this.ticket.getLatitude() ) ) )
                .andExpect( ( jsonPath( "longitude" ).value( this.ticket.getLongitude() ) ) )
                .andExpect( ( jsonPath( "status" ).value( this.ticket.getStatus().toString() ) ) )
        ;
    }

    @Test
    public void getTicketByIdRestForTicketThatDoesNotExist () throws Exception
    {
        when( ticketService.getById( this.ticket.getId() ) ).thenThrow( new ResourceNotFoundException() );
        mockMvc.perform( get( "/api/ticket/" + this.ticket.getId() ) )
                .andExpect( ( status().isNotFound() ) )
        ;
    }

    @Test
    public void getTicketsRest () throws Exception
    {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add( this.ticket );
        when( ticketService.getAll() ).thenReturn( tickets );
        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) )
                .andExpect( ( jsonPath( "$.[0].id" ).value( this.ticket.getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.[0].message" ).value( this.ticket.getMessage() ) ) )
                .andExpect( ( jsonPath( "$.[0].images" ).isArray() ) )
                .andExpect( ( jsonPath( "$.[0].images" ).isEmpty() ) )
                .andExpect( ( jsonPath( "$.[0].latitude" ).value( this.ticket.getLatitude() ) ) )
                .andExpect( ( jsonPath( "$.[0].longitude" ).value( this.ticket.getLongitude() ) ) )
                .andExpect( ( jsonPath( "$.[0].status" ).value( this.ticket.getStatus().toString() ) ) )
        ;
    }

    @Test
    public void getTicketsRestWhenNoTicketsExist () throws Exception
    {
        when( ticketService.getAll() ).thenReturn( new ArrayList<>() );
        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) )
        ;
    }

    @Test
    public void submitTicket () throws Exception
    {
        when( ticketService.create( any( Ticket.class ) ) ).thenReturn( this.ticket );

        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 12.131313," +
                        "\"longitude\": 14.141414}" ) )
                .andExpect( status().isCreated() )
                .andExpect( header().string( "location", "http://localhost/api/ticket/1" ) );
    }

    @Test
    public void submitTicketMalformedRequest () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"malformed\": \"malformed\"," +
                        "\"mlfmd\": 12.12," +
                        "\"mmd\": 14.12}" ) ).andExpect( status().isBadRequest() );
    }

    @Test
    public void submitTicketEmptyContent () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "" ) ).andExpect( status().isBadRequest() );
    }

    @Test
    public void submitTicketNullLatitudeAndOrLongitude () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"longitude\": 14.141414}" ) ).andExpect( status().isBadRequest() );

        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 14.141414}" ) ).andExpect( status().isBadRequest() );

        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"" ) ).andExpect( status().isBadRequest() );
    }

    @Test
    public void submitTicketNullMessage () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"latitude\": 12.131313," +
                        "\"longitude\": 14.141414}" ) ).andExpect( status().isBadRequest() );
    }
}
