package app.entities;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


@Entity
@EntityListeners ( AuditingEntityListener.class )
public class Ticket extends BaseEntity
{

    @NotNull
    @Size ( max = 2000 )
    private String message;
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;
    @CreatedDate
    @Temporal ( TemporalType.TIMESTAMP )
    private Date dateTime = new Date(); //// TODO: 09/02/2016 Refactor to use Java 8 time api + specified timezone
    @Size ( max = 250 )
    private String imageName;

    public Ticket ()
    {
    }

    public String getImageName ()
    {
        return imageName;
    }

    public void setImageName ( String imageName )
    {
        this.imageName = imageName;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage ( String message )
    {
        this.message = message;
    }

    public double getLatitude ()
    {
        return latitude;
    }

    public void setLatitude ( double latitude )
    {
        this.latitude = latitude;
    }

    public double getLongitude ()
    {
        return longitude;
    }

    public void setLongitude ( double longitude )
    {
        this.longitude = longitude;
    }

    public Date getDateTime ()
    {
        return dateTime;
    }

    public void setDateTime ( Date dateTime )
    {
        this.dateTime = dateTime;
    }

    @Override
    public String toString ()
    {
        return "ticket id : " + super.getId() + " message : " + this.message;
    }
}
