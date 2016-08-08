package app.entities;

import app.entities.enums.TicketStatus;
import app.pojo.TicketImage;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;


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

    @NotNull
    @Enumerated ( EnumType.STRING )
    private TicketStatus status = TicketStatus.NEW;

    @ElementCollection
    private Collection<TicketImage> images = new ArrayList<>();

    public Ticket ()
    {
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

    public TicketStatus getStatus ()
    {
        return status;
    }

    public void setStatus ( TicketStatus status )
    {
        this.status = status;
    }

    public Collection<TicketImage> getImages ()
    {
        return images;
    }

    public void setImages ( Collection<TicketImage> images )
    {
        this.images = images;
    }

    @Override
    public String toString ()
    {
        return "ticket id : " + super.getId() + " message : " + this.message;
    }
}
