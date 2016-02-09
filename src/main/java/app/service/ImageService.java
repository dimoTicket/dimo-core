package app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
public class ImageService
{

    private static final Logger logger = LoggerFactory.getLogger( ImageService.class );

    //Using original file name as filename
    public void saveMultipartFile ( MultipartFile multipartFile )
    {
        // TODO: 09/02/2016 : save location sto .properties
        File convFile = new File( "C:/Users/Alex/Desktop/temppics/" + multipartFile.getOriginalFilename() );
        try
        {
            multipartFile.transferTo( convFile );
            logger.info( "saved file to path : " + convFile.getAbsolutePath() );
        } catch ( IOException e )
        {
            logger.error( "Could not save file. Message : " + e.getMessage() );
            e.printStackTrace();
        }
    }
    // TODO: 09/02/2016 Get image for ticket id
}
