package integration;

import app.DimoApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = DimoApplication.class, webEnvironment = RANDOM_PORT )
@ActiveProfiles ( "integration-tests" )
@Rollback
@Transactional
public class UserRelatedTests
{

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp () throws Exception
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
    }

    @Test
    @Sql ( "/datasets/users.sql" )
    public void getAllUsers () throws Exception
    {
        mockMvc.perform( get( "/api/users/" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );
    }

    @Test
    @Sql ( "/datasets/users.sql" )
    public void getSpecificUser () throws Exception
    {
        mockMvc.perform( get( "/api/user/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$.length()", is( 2 ) ) ) //Should contain id and username only
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "username" ).value( "user1" ) ) );
    }

    @Test
    @Sql ( "/datasets/users.sql" )
    public void getSpecificUserThatDoesNotExist () throws Exception
    {
        mockMvc.perform( get( "/api/user/404" ) )
                .andExpect( ( status().isNotFound() ) );
    }

    @Test
    public void createUser () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"password\": \"12345678\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isCreated() ) );

        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$.[0].length()", is( 2 ) ) ) //Should contain id and username only
                .andExpect( ( jsonPath( "$.[0].username" ).value( "user1" ) ) );
    }

    @Test
    @Sql ( "/datasets/users.sql" )
    public void createUserWhenUsernameExists () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"password\": \"12345678\"," +
                        "\"email\": \"user1new@dimo.com\"}" ) )
                .andExpect( ( status().isInternalServerError() ) );

        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );
    }

    @Test
    @Sql ( "/datasets/users.sql" )
    public void createUserWhenEmailExists () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1new\"," +
                        "\"password\": \"12345678\"," +
                        "\"email\": \"test@dimo.com\"}" ) )
                .andExpect( ( status().isInternalServerError() ) );

        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );
    }

    @Test
    public void createUserPasswordTooSmall () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"password\": \"123\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isBadRequest() ) );

        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    @Test
    public void createUserNullFields () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isBadRequest() ) );

        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"password\": \"123\"}" ) )
                .andExpect( ( status().isBadRequest() ) );

        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "password\": \"123\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isBadRequest() ) );

        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }
}
