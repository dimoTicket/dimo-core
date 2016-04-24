package app.entities;

import app.entities.enums.TicketStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
    private Double latitude;
    @NotNull
    private Double longitude;
    @CreatedDate
    @Temporal ( TemporalType.TIMESTAMP )
    private Date dateTime = new Date();
    @NotNull
    @Enumerated ( EnumType.STRING )
    private TicketStatus status = TicketStatus.NEW;
    @Size ( max = 250 )
    private String imageName = "defaultimage.jpg";

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

    public Double getLatitude ()
    {
        return latitude;
    }

    public void setLatitude ( Double latitude )
    {
        this.latitude = latitude;
    }

    public Double getLongitude ()
    {
        return longitude;
    }

    public void setLongitude ( Double longitude )
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

    public TicketStatus getStatus ()
    {
        return status;
    }

    public void setStatus ( TicketStatus status )
    {
        this.status = status;
    }

    @Override
    public String toString ()
    {
        return "ticket id : " + super.getId() + " message : " + this.message;
    }
}
