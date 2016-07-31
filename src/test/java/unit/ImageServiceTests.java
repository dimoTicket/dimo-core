package unit;

import app.DimoApplication;
import app.entities.Ticket;
import app.entities.enums.TicketStatus;
import app.exceptions.service.ResourceNotFoundException;
import app.pojo.TicketImage;
import app.services.ImageService;
import app.services.TicketService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;


@RunWith ( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration ( classes = DimoApplication.class )
public class ImageServiceTests
{

    @Autowired
    @InjectMocks
    private ImageService imageService;

    @Mock
    private TicketService ticketService;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup ()
    {
        try
        {
            //Setting images path to the temporary folder created by @Rule
            Field images_folder = ImageService.class.getDeclaredField( "IMAGES_FOLDER" );
            images_folder.setAccessible( true );
            images_folder.set( String.class, tempFolder.getRoot().getAbsolutePath() );
        } catch ( IllegalAccessException | NoSuchFieldException e )
        {
            e.printStackTrace();
        }

        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void saveImageWhenTicketImageCollectionIsEmpty ()
    {
        Ticket ticket = this.getMockTicket();
        when( this.ticketService.getById( 1L ) ).thenReturn( ticket );
        when( this.ticketService.update( ticket ) ).thenReturn( ticket );

        MockMultipartFile mockImage =
                new MockMultipartFile( "image", "image name.jpg", "image/jpeg", new byte[ 0 ] );

        this.imageService.saveImage( 1L, mockImage );

        assertThat( ticket.getImages().size(), is( 1 ) );
        assertThat( ticket.getImages().stream()
                .anyMatch( ti -> ti.getImageName().equals( mockImage.getOriginalFilename() ) ), is( true ) );
        assertThat( tempFolder.getRoot().listFiles().length, is( 1 ) );
        assertThat( tempFolder.getRoot().listFiles()[ 0 ].getName(), is( "image name.jpg" ) );
    }

    @Test
    public void saveImageWhenTicketImageCollectionIsEmptyConsecutiveCalls ()
    {
        Ticket ticket = this.getMockTicket();
        when( this.ticketService.getById( 1L ) ).thenReturn( ticket );
        when( this.ticketService.update( ticket ) ).thenReturn( ticket );

        MockMultipartFile mockImage1 =
                new MockMultipartFile( "image", "image name.jpg", "image/jpeg", new byte[ 0 ] );
        MockMultipartFile mockImage2 =
                new MockMultipartFile( "image", "image name2.jpg", "image/jpeg", new byte[ 0 ] );

        this.imageService.saveImage( 1L, mockImage1 );
        this.imageService.saveImage( 1L, mockImage2 );

        assertThat( ticket.getImages().size(), is( 2 ) );
        assertThat( ticket.getImages().stream()
                .anyMatch( ti -> ti.getImageName().equals( mockImage1.getOriginalFilename() ) ), is( true ) );
        assertThat( ticket.getImages().stream()
                .anyMatch( ti -> ti.getImageName().equals( mockImage2.getOriginalFilename() ) ), is( true ) );
        assertThat( tempFolder.getRoot().listFiles().length, is( 2 ) );
        assertThat( tempFolder.getRoot().listFiles()[ 0 ].getName(), is( mockImage1.getOriginalFilename() ) );
        assertThat( tempFolder.getRoot().listFiles()[ 1 ].getName(), is( mockImage2.getOriginalFilename() ) );
    }

    @Test
    public void saveImageForTicketThatDoesNotExist () throws Exception
    {
        when( this.ticketService.getById( 1L ) ).thenThrow( new ResourceNotFoundException() );
        this.thrown.expect( ResourceNotFoundException.class );
        MockMultipartFile mockImage =
                new MockMultipartFile( "image", "image name.jpg", "image/jpeg", new byte[ 0 ] );
        this.imageService.saveImage( 1L, mockImage );
    }

    @Test
    public void saveImageWhenItAlreadyExistsForTicket () throws Exception
    {
        Ticket ticket = this.getMockTicket();
        MockMultipartFile mockImage =
                new MockMultipartFile( "image", "image name.jpg", "image/jpeg", new byte[ 0 ] );
        ticket.getImages().add( new TicketImage( mockImage.getOriginalFilename() ) );
        when( this.ticketService.getById( 1L ) ).thenReturn( ticket );

        this.thrown.expect( IllegalArgumentException.class );
        this.imageService.saveImage( 1L, mockImage );
    }

    @Test
    public void getTicketImagesWhenTicketHasNoImages () throws Exception
    {
        Ticket ticket = this.getMockTicket();
        when( this.ticketService.getById( 1L ) ).thenReturn( ticket );

        Collection<File> images = this.imageService.getTicketImages( 1L );
        assertThat( images.size(), is( 0 ) );
    }

    @Test
    public void getTicketImagesWhenTicketHasOneImage () throws Exception
    {
        Ticket ticket = this.getMockTicket();
        when( this.ticketService.getById( 1L ) ).thenReturn( ticket );
        when( this.ticketService.update( ticket ) ).thenReturn( ticket );
        MockMultipartFile mockImage =
                new MockMultipartFile( "image", "image name.jpg", "image/jpeg", new byte[ 0 ] );
        this.imageService.saveImage( 1L, mockImage );

        Collection<File> images = this.imageService.getTicketImages( 1L );
        assertThat( images.size(), is( 1 ) );
        assertThat( images.stream().findFirst().get().getAbsolutePath(),
                is( tempFolder.getRoot().listFiles()[ 0 ].getAbsolutePath() ) );
    }

    public Ticket getMockTicket ()
    {
        Ticket ticket = new Ticket();
        ticket.setId( 1L );
        ticket.setMessage( "Ticket message 1" );
        ticket.setImages( new ArrayList<>() );
        ticket.setLatitude( 12.345678 );
        ticket.setLongitude( 25.579135 );
        ticket.setStatus( TicketStatus.NEW );
        return ticket;
    }

}
