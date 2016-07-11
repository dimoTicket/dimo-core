package app.exceptions;

import app.exceptions.service.BadRequestException;
import app.exceptions.service.ResourceNotFoundException;
import app.exceptions.pojo.ErrorDetails;
import app.exceptions.pojo.ValidationError;
import app.exceptions.service.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler
{

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler ( ResourceNotFoundException.class )
    public ResponseEntity handleResourceNotFoundException ( ResourceNotFoundException ex )
    {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setDetails( ex.getMessage() );
        errorDetails.setDeveloperMessage( ex.getClass().getName() );
        errorDetails.setStatus( HttpStatus.NOT_FOUND.value() );
        errorDetails.setTitle( "Resource not found" );
        errorDetails.setTimestamp( new Date().getTime() );
        return new ResponseEntity<>( errorDetails, HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler ( UserServiceException.class )
    public ResponseEntity handleUserServiceException ( UserServiceException ex )
    {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setDetails( ex.getMessage() );
        errorDetails.setDeveloperMessage( ex.getClass().getName() );
        errorDetails.setStatus( HttpStatus.INTERNAL_SERVER_ERROR.value() );
        errorDetails.setTitle( "Error when validating user" );
        errorDetails.setTimestamp( new Date().getTime() );
        return new ResponseEntity<>( errorDetails, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    @ExceptionHandler ( BadRequestException.class )
    public ResponseEntity handleBadRequestException ( BadRequestException ex )
    {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setDetails( ex.getMessage() );
        errorDetails.setDeveloperMessage( ex.getClass().getName() );
        errorDetails.setStatus( HttpStatus.BAD_REQUEST.value() );
        errorDetails.setTitle( "Bad request" );
        errorDetails.setTimestamp( new Date().getTime() );
        return new ResponseEntity<>( errorDetails, HttpStatus.BAD_REQUEST );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid ( MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request )
    {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setDetails( "Input validation failed" );
        errorDetails.setDeveloperMessage( ex.getClass().getName() );
        errorDetails.setStatus( HttpStatus.BAD_REQUEST.value() );
        errorDetails.setTitle( "Validation failed" );
        errorDetails.setTimestamp( new Date().getTime() );

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.stream().forEach( fe -> {
            List<ValidationError> validationErrorList = errorDetails.getValidationErrors().get( fe.getField() );
            if ( validationErrorList == null )
            {
                validationErrorList = new ArrayList<>();
                errorDetails.getValidationErrors().put( fe.getField(),
                        validationErrorList );
            }
            ValidationError validationError = new ValidationError();
            validationError.setCode( fe.getCode() );
            validationError.setMessage( this.messageSource.getMessage( fe, null ) ); //Auto resolve from messages.properties
            validationErrorList.add( validationError );
        } );
        return handleExceptionInternal( ex, errorDetails, headers, status, request );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable ( HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request )
    {
        ErrorDetails errorDetail = new ErrorDetails();
        errorDetail.setTimestamp( new Date().getTime() );
        errorDetail.setStatus( status.value() );
        errorDetail.setTitle( "Message Not Readable" );
        errorDetail.setDetails( ex.getMessage() );
        errorDetail.setDeveloperMessage( ex.getClass().getName() );
        return handleExceptionInternal( ex, errorDetail, headers, status, request );
    }
}
