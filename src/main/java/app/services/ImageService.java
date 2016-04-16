package app.services;

import app.entities.Ticket;
import app.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
public class ImageService
{

    private static final Logger logger = LoggerFactory.getLogger( ImageService.class );
    private static String IMAGES_FOLDER = "/home/pics/"; // TODO: 10/02/2016 move to .properties
    @Autowired
    private TicketRepository ticketRepository;

    //Using original file name as filename
    public void saveMultipartFile ( Long ticketId, MultipartFile multipartFile )
    {
        Ticket ticket = ticketRepository.findOne( ticketId );
        //prevents re-setting a picture
        if ( ticket.getImageName() != null )
        {
            throw new IllegalArgumentException( "Image already set for ticketId=" + ticketId );
        }
        // TODO: 09/02/2016 : save location sto .properties
        File convFile = new File( IMAGES_FOLDER + multipartFile.getOriginalFilename() );
        try
        {
            multipartFile.transferTo( convFile );
            logger.info( "saved file to path : " + convFile.getAbsolutePath() );
            ticket.setImageName( multipartFile.getOriginalFilename() );
            ticketRepository.saveAndFlush( ticket );
        } catch ( IOException e )
        {
            logger.error( "Could not save file. Message : " + e.getMessage() );
            e.printStackTrace();
        } catch ( IllegalArgumentException e2 )
        {
            logger.error( "Illegal argument passed in findOne of TicketRepository" );
        }
    }

    public File findPictureFileOfTicketId ( Long ticketId )
    {
        return new File( IMAGES_FOLDER + this.ticketRepository.findOne( ticketId ).getImageName() );
    }
}
