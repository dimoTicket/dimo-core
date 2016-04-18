package app.entities;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;


@Entity
public class Authority extends BaseEntity implements GrantedAuthority
{

    public Authority ()
    {
    }

    public Authority ( String authority )
    {
        this.authority = authority;
    }

    @NotNull
    @Column ( unique = true )
    private String authority;

    public void setAuthority ( String authority )
    {
        this.authority = authority;
    }

    @Override
    public String getAuthority ()
    {
        return this.authority;
    }
}
