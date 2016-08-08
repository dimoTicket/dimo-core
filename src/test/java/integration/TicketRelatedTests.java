package integration;

import app.DimoApplication;
import app.entities.enums.TicketStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = DimoApplication.class, webEnvironment = RANDOM_PORT )
@ActiveProfiles ( "integration-tests" )
@Rollback
@Transactional
@DirtiesContext ( classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD )
public class TicketRelatedTests
{

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @Before
    public void setUp () throws Exception
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
    }

    @Test
    @Sql ( "/datasets/tickets.sql" )
    public void getAllTickets () throws Exception
    {
        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 3 ) ) );
    }

    @Test
    @Sql ( "/datasets/tickets.sql" )
    public void getSpecificTicket () throws Exception
    {
        mockMvc.perform( get( "/api/ticket/3" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( ( jsonPath( "id" ).value( 3 ) ) )
                .andExpect( ( jsonPath( "message" ).value( "Ticket Message3" ) ) )
                .andExpect( ( jsonPath( "images" ).isArray() ) )
                .andExpect( ( jsonPath( "images" ).isEmpty() ) )
                .andExpect( ( jsonPath( "latitude" ).value( 40.631756 ) ) )
                .andExpect( ( jsonPath( "longitude" ).value( 22.951907 ) ) )
                .andExpect( ( jsonPath( "status" ).value( TicketStatus.ASSIGNED.toString() ) ) );
    }

    @Test
    public void createTicket () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 40.631756," +
                        "\"longitude\": 22.951907}" ) )
                .andExpect( status().isCreated() )
                .andExpect( header().string( "location", "http://localhost/api/ticket/1" ) );

        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );

        mockMvc.perform( get( "/api/ticket/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "message" ).value( "MockMessage" ) ) )
                .andExpect( ( jsonPath( "images" ).isArray() ) )
                .andExpect( ( jsonPath( "images" ).isEmpty() ) )
                .andExpect( ( jsonPath( "latitude" ).value( 40.631756 ) ) )
                .andExpect( ( jsonPath( "longitude" ).value( 22.951907 ) ) )
                .andExpect( ( jsonPath( "status" ).value( TicketStatus.NEW.toString() ) ) );
    }
}
