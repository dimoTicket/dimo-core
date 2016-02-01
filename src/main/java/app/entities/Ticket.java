package app.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Ticket
{

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private long id;
    private String message;
    private float latitude;
    private float longitude;

    public Ticket ()
    {
    }

    public float getLatitude ()
    {
        return latitude;
    }

    public float getLongitude ()
    {
        return longitude;
    }

    public Ticket ( String message, float latitude, float longitude )
    {
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId ()
    {
        return id;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage ( String message )
    {
        this.message = message;
    }

    public void setLatitude ( float latitude )
    {
        this.latitude = latitude;
    }

    public void setLongitude ( float longitude )
    {
        this.longitude = longitude;
    }

    @Override
    public String toString ()
    {
        return "ticket id : " + this.id + " message : " + this.message;
    }
}
