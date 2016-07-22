package app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;


@Entity
public class User extends BaseEntity implements UserDetails
{

    @NotNull
    @Column ( unique = true )
    private String username;

    @NotNull
    @JsonIgnore
    @Size ( min = 8 )
    private String password;

    @Email
    @NotNull
    @JsonIgnore
    @Column ( unique = true )
    private String email;

    @ManyToMany ( fetch = FetchType.EAGER )
    @NotNull
    @JsonIgnore
    private Collection<Authority> authorities;

    public User ()
    {
    }

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

    public void setAuthorities ( Collection<Authority> authorities )
    {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities ()
    {
        return this.authorities;
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
    @JsonIgnore
    public boolean isAccountNonExpired ()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked ()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired ()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled ()
    {
        return true;
    }

    @Override
    @JsonIgnore
    public Date getCreatedAt ()
    {
        return super.getCreatedAt();
    }

    @Override
    public boolean equals ( Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode ()
    {
        return super.hashCode();
    }
}
