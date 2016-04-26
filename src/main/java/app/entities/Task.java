package app.entities;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;


@Entity
public class Task extends BaseEntity
{

    @OneToOne
    @NotNull
    private Ticket ticket;

    @ManyToMany
    @NotNull
    @Size ( min = 1 )
    private Collection<User> users;

    @CreatedDate
    @Temporal ( TemporalType.TIMESTAMP )
    private Date createdAt = new Date();

    public Task ()
    {
    }

    public Date getCreatedAt ()
    {
        return createdAt;
    }

    public void setCreatedAt ( Date createdAt )
    {
        this.createdAt = createdAt;
    }

    public Ticket getTicket ()
    {
        return ticket;
    }

    public void setTicket ( Ticket ticket )
    {
        this.ticket = ticket;
    }

    public Collection<User> getUsers ()
    {
        return users;
    }

    public void setUsers ( Collection<User> users )
    {
        this.users = users;
    }
}
