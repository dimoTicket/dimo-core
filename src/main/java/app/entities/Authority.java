package app.entities;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;


@Entity
public class Authority extends BaseEntity implements GrantedAuthority
{

    @NotNull
    @Column ( unique = true )
    private String authorityString;

    public Authority ()
    {
    }

    public Authority ( String authorityString )
    {
        this.authorityString = authorityString;
    }

    public void setAuthorityString ( String authorityString )
    {
        this.authorityString = authorityString;
    }

    @Override
    public String getAuthority ()
    {
        return this.authorityString;
    }
}
