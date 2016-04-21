package app.entities;

import app.security.SecurityConfiguration;
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


@Entity
public class User extends BaseEntity implements UserDetails
{

    @NotNull
    @Column ( unique = true )
    private String username;
    @NotNull
    @Size ( min = 8 )
    @JsonIgnore
    private String password;
    @Email
    @NotNull
    @Column ( unique = true )
    private String email;
    @ManyToMany ( fetch = FetchType.EAGER )
    private Collection<Authority> authorities;

    public User ()
    {
    }

    public void setUsername ( String username )
    {
        this.username = username;
    }

    /**
     * Also encrypts the password
     * @param password RawPassword
     */
    public void setPassword ( String password )
    {
        this.password = SecurityConfiguration.passwordEncoder.encode( password );
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
