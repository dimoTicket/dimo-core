package app.entities;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;


@MappedSuperclass
class BaseEntity
{

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    protected Long id;

    @CreatedDate
    @Temporal ( TemporalType.TIMESTAMP )
    private Date createdAt = new Date();

    public Long getId ()
    {
        return id;
    }

    public void setId ( Long id )
    {
        this.id = id;
    }

    public Date getCreatedAt ()
    {
        return createdAt;
    }

    public void setCreatedAt ( Date createdAt )
    {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals ( Object o )
    {
        if ( this == o ) return true;
        if ( !( o instanceof BaseEntity ) ) return false;
        BaseEntity that = ( BaseEntity )o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode ()
    {
        return Objects.hash( id );
    }
}
