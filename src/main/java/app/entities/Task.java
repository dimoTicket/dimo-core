package app.entities;

import app.validation.tags.TaskDependenciesDbValidation;
import app.validation.annotations.TicketExists;
import app.validation.annotations.UsersExist;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;


@Entity
public class Task extends BaseEntity
{

    @OneToOne ( cascade = CascadeType.REFRESH )
    @NotNull
    @TicketExists ( groups = TaskDependenciesDbValidation.class )
    private Ticket ticket;

    @ManyToMany ( cascade = CascadeType.REFRESH )
    @NotNull
    @Size ( min = 1 )
    @UsersExist ( groups = TaskDependenciesDbValidation.class )
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
