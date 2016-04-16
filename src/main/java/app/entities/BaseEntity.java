package app.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class BaseEntity
{

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    protected Long id;

    public Long getId ()
    {
        return id;
    }

    public void setId ( Long id )
    {
        this.id = id;
    }
}
