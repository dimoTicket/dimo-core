package springmvc;

import app.controllers.UserController;
import app.entities.User;
import app.exceptions.RestExceptionHandler;
import app.exceptions.service.UserIdDoesNotExistException;
import app.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith ( MockitoJUnitRunner.class )
@ActiveProfiles ( "unit-tests" )
public class UserControllerTests
{

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup ()
    {
        MockitoAnnotations.initMocks( this );
        mockMvc = standaloneSetup( new UserController( this.userService ) )
                .setControllerAdvice( new RestExceptionHandler() )
                .build();
    }

    @Test
    public void createUser () throws Exception
    {
        doNothing().when( userService ).createUser( any() );

        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"password\": \"12345678\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isCreated() ) );
    }

    @Test
    public void createUserMalformedRequest () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"zzusername\": \"zzuser1\"," +
                        "\"zzpassword\": \"zz12345678\"," +
                        "\"zzemail\": \"zzuser1@dimo.com\"}" ) )
                .andExpect( ( status().isBadRequest() ) );
    }

    @Test
    public void createUserEmptyContent () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "" ) )
                .andExpect( ( status().isBadRequest() ) );
    }

    @Test
    public void createUserEmptyEmail () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"password\": \"12345678\"}" ) )
                .andExpect( ( status().isBadRequest() ) );
    }

    @Test
    public void createUserEmptyPassword () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"username\": \"user1\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isBadRequest() ) );
    }

    @Test
    public void createUserEmptyUsername () throws Exception
    {
        mockMvc.perform( post( "/api/register" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"password\": \"12345678\"," +
                        "\"email\": \"user1@dimo.com\"}" ) )
                .andExpect( ( status().isBadRequest() ) );
    }

    @Test
    public void getAllUsers () throws Exception
    {
        List<User> users = new ArrayList<>();
        users.add( getMockUser() );
        when( userService.getAllUsers() ).thenReturn( users );

        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$.[0].length()", is( 2 ) ) ) //Should contain id and username only
                .andExpect( ( jsonPath( "$.[0].id" ).value( getMockUser().getId().intValue() ) ) )
                .andExpect( ( jsonPath( "$.[0].username" ).value( getMockUser().getUsername() ) ) );
    }

    @Test
    public void getAllUsersWhenNoUsersAreRegistered () throws Exception
    {
        List<User> users = Collections.emptyList();
        when( userService.getAllUsers() ).thenReturn( users );
        mockMvc.perform( get( "/api/users" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    @Test
    public void getSpecificUser () throws Exception
    {
        User user = getMockUser();
        when( userService.loadById( user.getId() ) ).thenReturn( user );
        mockMvc.perform( get( "/api/user/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( ( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) ) )
                .andExpect( jsonPath( "$.length()", is( 2 ) ) ) //Should contain id and username only
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "username" ).value( "user1" ) ) );
    }

    @Test
    public void getSpecificUserThatDoesNotExist () throws Exception
    {
        when( userService.loadById( any() ) ).thenThrow( new UserIdDoesNotExistException() );
        mockMvc.perform( get( "/api/user/404" ) )
                .andExpect( ( status().isNotFound() ) );
    }

    private User getMockUser ()
    {
        User user = new User();
        user.setId( 1L );
        user.setAuthorities( Collections.emptyList() );
        user.setUsername( "user1" );
        user.setEmail( "user1@dimo.com" );
        user.setPassword( "12345678" );
        return user;
    }
}
