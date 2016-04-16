package app.entities;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;


@Entity
public class User extends BaseEntity implements UserDetails
{

    @NotNull
    @Column ( unique = true )
    private String username;
    @NotNull
    @Min ( value = 8 )
    private String password;
    @Email
    @NotNull
    @Column ( unique = true )
    private String email;

    public void setUsername ( String username )
    {
        this.username = username;
    }

    public void setPassword ( String password )
    {
        this.password = password;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail ( String email )
    {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities ()
    {
        return Collections.emptyList();
    }

    @Override
    public String getPassword ()
    {
        return this.password;
    }

    @Override
    public String getUsername ()
    {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired ()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked ()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired ()
    {
        return true;
    }

    @Override
    public boolean isEnabled ()
    {
        return true;
    }
}
