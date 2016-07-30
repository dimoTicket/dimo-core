package app.pojo;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Embeddable
public class TicketImage
{

    @NotNull
    private String imageName;

    @CreatedDate
    @Temporal ( TemporalType.TIMESTAMP )
    private Date createdAt = new Date();

    public TicketImage ()
    {
    }

    public TicketImage ( String imageName )
    {
        this.imageName = imageName;
    }

    public String getImageName ()
    {
        return imageName;
    }

    public void setImageName ( String imageName )
    {
        this.imageName = imageName;
    }

    public Date getCreatedAt ()
    {
        return createdAt;
    }

    public void setCreatedAt ( Date createdAt )
    {
        this.createdAt = createdAt;
    }
}
