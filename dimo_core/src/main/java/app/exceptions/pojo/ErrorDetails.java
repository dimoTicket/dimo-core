package app.exceptions.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ErrorDetails
{

    private String title;
    private int status;
    private String details;
    private long timestamp;
    private String developerMessage;
    private Map<String, List<ValidationError>> validationErrors = new HashMap<>();

    public String getTitle ()
    {
        return title;
    }

    public void setTitle ( String title )
    {
        this.title = title;
    }

    public int getStatus ()
    {
        return status;
    }

    public void setStatus ( int status )
    {
        this.status = status;
    }

    public String getDetails ()
    {
        return details;
    }

    public void setDetails ( String details )
    {
        this.details = details;
    }

    public long getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp ( long timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getDeveloperMessage ()
    {
        return developerMessage;
    }

    public void setDeveloperMessage ( String developerMessage )
    {
        this.developerMessage = developerMessage;
    }

    public Map<String, List<ValidationError>> getValidationErrors ()
    {
        return validationErrors;
    }

    public void setValidationErrors ( Map<String, List<ValidationError>> validationErrors )
    {
        this.validationErrors = validationErrors;
    }
}
