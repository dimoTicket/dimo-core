package app.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;


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

    public Task ()
    {
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
