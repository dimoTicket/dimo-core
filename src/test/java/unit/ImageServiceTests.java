package unit;

import app.DimoApplication;
import app.entities.Ticket;
import app.entities.enums.TicketStatus;
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

import java.lang.reflect.Field;
import java.util.ArrayList;

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
        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void saveImageWhenTicketImageCollectionIsEmpty ()
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
