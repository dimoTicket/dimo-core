package integration;

import app.DimoApplication;
import app.entities.enums.TicketStatus;
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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = DimoApplication.class, webEnvironment = RANDOM_PORT )
@ActiveProfiles ( "integration-tests" )
@Rollback
@Transactional
public class TaskRelatedTests
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
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void getAllTasks () throws Exception
    {
        mockMvc.perform( get( "/api/tasks" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( jsonPath( "$", hasSize( 3 ) ) );
    }

    @Test
    public void getAllTasksWhenNoTasksExist () throws Exception
    {
        mockMvc.perform( get( "/api/tasks" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void getTaskById () throws Exception
    {
        mockMvc.perform( get( "/api/task/3" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 3 ) ) )
                .andExpect( ( jsonPath( "createdAt" ).isNotEmpty() ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 3 ) ) )
                .andExpect( ( jsonPath( "$.ticket.message" ).value( "Ticket Message3" ) ) )
                .andExpect( ( jsonPath( "$.users" ) ).isArray() )
                .andExpect( ( jsonPath( "$.users", hasSize( 2 ) ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) )
                .andExpect( ( jsonPath( "$.users[0].password" ).doesNotExist() ) )
                .andExpect( ( jsonPath( "$.users[0].email" ).doesNotExist() ) )
                .andExpect( ( jsonPath( "$.users[1].id" ).value( 2 ) ) )
                .andExpect( ( jsonPath( "$.users[1].username" ).value( "user2" ) ) )
                .andExpect( ( jsonPath( "$.users[1].password" ).doesNotExist() ) )
                .andExpect( ( jsonPath( "$.users[1].email" ).doesNotExist() ) );
    }

    @Test
    public void getTaskByIdForTaskThatDoesNotExist () throws Exception
    {
        mockMvc.perform( get( "/api/task/3" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql" } )
    public void submitTask () throws Exception
    {
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1},\n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isCreated() )
                .andExpect( header().string( "location", "http://localhost/api/task/1" ) );

        mockMvc.perform( get( "/api/task/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.message" ).value( "Ticket Message1" ) ) )
                .andExpect( ( jsonPath( "$.ticket.status" ).value( TicketStatus.ASSIGNED.toString() ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql" } )
    public void submitTaskForTicketThatDoesNotExist () throws Exception
    {
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 404}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1}\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void submitTaskForTicketThatAlreadyHasATask () throws Exception
    {
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1}, \n" +
                        "  \"users\": [\n" +
                        "  {\n" +
                        "    \"id\": 1}\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql" } )
    public void submitTaskBadRequests () throws Exception
    {
        //Malformed request
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"malformed\": {\"id\": 1,\n" +
                        "  \"message\": \"Ticket Message1\",\n" +
                        "  \"latitude\": 40.631756,\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"malformed\": \"MockName\"\n" +
                        "  }\n" +
                        "]\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );

        //Random ticket content without users array
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{" +
                        "\"message\": \"MockMessage\"," +
                        "\"latitude\": 12.131313," +
                        "\"longitude\": 14.141414}" ) )
                .andExpect( status().isBadRequest() );

        //Empty users array
        mockMvc.perform( post( "/api/task/newtask" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"ticket\": {\"id\": 1},\n" +
                        " \"users\": []\n" +
                        "}" ) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void addUsersToTask () throws Exception
    {
        //Add user with id 2 to the task with id 1
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isOk() );

        //Get to verify
        mockMvc.perform( get( "/api/task/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users" ) ).isArray() )
                .andExpect( ( jsonPath( "$.users", hasSize( 2 ) ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) )
                .andExpect( ( jsonPath( "$.users[1].id" ).value( 2 ) ) )
                .andExpect( ( jsonPath( "$.users[1].username" ).value( "user2" ) ) );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void addUsersToTaskForTaskThatDoesNotExist () throws Exception
    {
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 4,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void addUsersToTaskBadRequests () throws Exception
    {
        //Some given users don't exist
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isBadRequest() );

        //Users already in task
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 3,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 3},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 1},{\n" +
                        "    \"id\": 2}]}" ) )
                .andDo( print() )
                .andExpect( status().isOk() );

        //Empty given user array
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": []}" ) )
                .andExpect( status().isOk() );

        //No user array
        mockMvc.perform( post( "/api/task/addusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1}}" ) )
                .andExpect( status().isBadRequest() );

        //Get to verify
        mockMvc.perform( get( "/api/task/1" ) )
                .andExpect( ( status().isOk() ) )
                .andDo( print() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users" ) ).isArray() )
                .andExpect( ( jsonPath( "$.users", hasSize( 1 ) ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void removeUsersFromTask () throws Exception
    {
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isOk() );

        mockMvc.perform( get( "/api/task/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users", hasSize( 1 ) ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void removeUsersFromTaskBadRequests () throws Exception
    {
        //Task does not exist
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 404,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2}]}" ) )
                .andExpect( status().isNotFound() );

        //All given users don't exist
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 3},{\n" +
                        "    \"id\": 4}]}" ) )
                .andExpect( status().isBadRequest() );

        //Some given users don't exist
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": [{\n" +
                        "    \"id\": 2},{\n" +
                        "    \"id\": 3}]}" ) )
                .andExpect( status().isBadRequest() );

        //Empty given user array
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1},\n" +
                        "  \"users\": []}" ) )
                .andExpect( status().isOk() );

        //Absent given user array
        mockMvc.perform( post( "/api/task/removeusers" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( "{\"id\": 1,\n" +
                        "    \"ticket\": {\n" +
                        "      \"id\": 1}}" ) )
                .andExpect( status().isBadRequest() );

        //Get to verify
        mockMvc.perform( get( "/api/task/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users", hasSize( 1 ) ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) );
    }

    @Test
    @Sql ( scripts = {
            "/datasets/tickets.sql",
            "/datasets/users.sql",
            "/datasets/tasks.sql" } )
    public void changeStatus () throws Exception
    {
        mockMvc.perform( post( "/api/task/changestatus" )
                .contentType( MediaType.APPLICATION_JSON_UTF8 )
                .param( "ticketId", "1" )
                .param( "status", TicketStatus.REJECTED.toString() ) )
                .andExpect( status().isOk() );

        mockMvc.perform( get( "/api/task/1" ) )
                .andExpect( ( status().isOk() ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8 ) )
                .andExpect( ( jsonPath( "id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "createdAt" ).isNotEmpty() ) )
                .andExpect( ( jsonPath( "$.ticket.id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.ticket.message" ).value( "Ticket Message1" ) ) )
                .andExpect( ( jsonPath( "$.ticket.status" ).value( "REJECTED" ) ) )
                .andExpect( ( jsonPath( "$.users" ) ).isArray() )
                .andExpect( ( jsonPath( "$.users", hasSize( 1 ) ) ) )
                .andExpect( ( jsonPath( "$.users[0].id" ).value( 1 ) ) )
                .andExpect( ( jsonPath( "$.users[0].username" ).value( "user1" ) ) )
                .andExpect( ( jsonPath( "$.users[0].password" ).doesNotExist() ) )
                .andExpect( ( jsonPath( "$.users[0].email" ).doesNotExist() ) );
    }

}
