package integration;

import app.DimoApplication;
import app.entities.enums.TicketStatus;
import app.services.ImageService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = DimoApplication.class, webEnvironment = RANDOM_PORT )
@ActiveProfiles ( "integration-tests" )
@Transactional
@Rollback
public class TicketRelatedTests
{

    @Autowired
    private WebApplicationContext wac;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private MockMvc mockMvc;

    @Before
    public void setUp () throws Exception
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
    }

    @Test
    @Sql ( scripts = "/datasets/tickets.sql" )
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
    @Sql ( "/datasets/tickets.sql" )
    public void getSpecificTicketThatDoesNotExist () throws Exception
    {
        mockMvc.perform( get( "/api/ticket/4" ) )
                .andExpect( ( status().isNotFound() ) );
    }

    @Test
    public void createTicket () throws Exception
    {
        MvcResult mvcResult = mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 40.631756," +
                        "\"longitude\": 22.951907}" ) )
                .andExpect( status().isCreated() )
                .andReturn();

        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );

        String createdTicketLocation = mvcResult.getResponse().getHeaderValue( "Location" ).toString();
        int lastSlashIndex = createdTicketLocation.lastIndexOf( "/" );
        String createdTicketId = createdTicketLocation.substring( lastSlashIndex + 1 );
        mockMvc.perform( get( "/api/ticket/" + createdTicketId ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( ( jsonPath( "id" ).value( Integer.valueOf( createdTicketId ) ) ) )
                .andExpect( ( jsonPath( "message" ).value( "MockMessage" ) ) )
                .andExpect( ( jsonPath( "images" ).isArray() ) )
                .andExpect( ( jsonPath( "images" ).isEmpty() ) )
                .andExpect( ( jsonPath( "latitude" ).value( 40.631756 ) ) )
                .andExpect( ( jsonPath( "longitude" ).value( 22.951907 ) ) )
                .andExpect( ( jsonPath( "status" ).value( TicketStatus.NEW.toString() ) ) );
    }

    @Test
    public void createTicketMalformedRequest () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"123message\": \"MockMessage\"," +
                        "\"123latitude\": 40.631756," +
                        "\"123longitude\": 22.951907}" ) )
                .andExpect( status().isBadRequest() );

        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    @Test
    public void createTicketEmptyContent () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "" ) ).andExpect( status().isBadRequest() );

        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    @Test
    public void createTicketNullFields () throws Exception
    {
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"longitude\": 14.141414}" ) )
                .andExpect( status().isBadRequest() );
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 14.141414}" ) )
                .andExpect( status().isBadRequest() );
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"" ) )
                .andExpect( status().isBadRequest() );
        mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"latitude\": 12.131313," +
                        "\"longitude\": 14.141414}" ) )
                .andExpect( status().isBadRequest() );

        mockMvc.perform( get( "/api/tickets/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    @Test
    @Sql ( "/datasets/tickets.sql" )
    public void uploadImageForExistingTicket () throws Exception
    {
        this.changeImageServicePathToTempFolder();

        URL picUrl = this.getClass().getClassLoader().getResource( "images/thiswill.jpg" );
        File picFile = new File( picUrl.getFile() );

        MockMultipartFile mockImage =
                new MockMultipartFile( "image", picFile.getName(), "image/jpeg",
                        FileCopyUtils.copyToByteArray( picFile ) );

        mockMvc.perform( fileUpload( "/api/ticket/newimage" )
                .file( mockImage )
                .param( "ticketId", "1" ) )
                .andExpect( status().isCreated() );
    }

    @Test
    public void createTicketAndUploadImages () throws Exception
    {
        this.changeImageServicePathToTempFolder();

        MvcResult mvcResult = mockMvc.perform( post( "/api/ticket/newticket" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 12.131313," +
                        "\"longitude\": 14.141414}" ) )
                .andExpect( status().isCreated() )
                .andReturn();

        String createdTicketLocation = mvcResult.getResponse().getHeaderValue( "Location" ).toString();
        int lastSlashIndex = createdTicketLocation.lastIndexOf( "/" );
        String createdTicketId = createdTicketLocation.substring( lastSlashIndex + 1 );

        URL picUrl = this.getClass().getClassLoader().getResource( "images/thiswill.jpg" );
        File picFile = new File( picUrl.getFile() );
        MockMultipartFile mockImage =
                new MockMultipartFile( "image", picFile.getName(), "image/jpeg",
                        FileCopyUtils.copyToByteArray( picFile ) );

        mockMvc.perform( fileUpload( "/api/ticket/newimage" )
                .file( mockImage )
                .param( "ticketId", createdTicketId ) )
                .andExpect( status().isCreated() );

        mockMvc.perform( get( "/api/tickets/" ) )
                .andDo( print() )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );

        // TODO: 19/8/2016 assertions more
    }

    private void changeImageServicePathToTempFolder ()
    {
        //Setting images path to the temporary folder created by @Rule
        try
        {
            Field images_folder = ImageService.class.getDeclaredField( "IMAGES_FOLDER" );
            images_folder.setAccessible( true );
            images_folder.set( String.class, temporaryFolder.getRoot().getAbsolutePath() );
        } catch ( IllegalAccessException | NoSuchFieldException e )
        {
            e.printStackTrace();
        }
    }

}
