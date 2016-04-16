package app.entities;

import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
public class User extends BaseEntity
{

    @Email
    @NotNull
    @Column ( unique = true )
    private String email;
    @NotNull
    @Size ( min = 8 )
    private String password;

    public User ()
    {
    }

    public String getEmail ()
    {
        return email;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setEmail ( String email )
    {
        this.email = email;
    }

    public void setPassword ( String password )
    {
        this.password = password;
    }

    @Override
    public String toString ()
    {
        return "user id : " + super.getId() + " email : " + email;
    }
}
