package app.security;

import app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{

    public static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserService userService;

    @Override
    protected void configure ( HttpSecurity http ) throws Exception
    {
        http.authorizeRequests()
                .accessDecisionManager( this.getAccessDecisionManager() )
                .antMatchers( "/*" ).permitAll()//hasAuthority( Authorities.User )
                .and().formLogin() // TODO: 21/4/2016 Use a custom login page
                .and().logout().logoutSuccessUrl( "/" );
        http.csrf().disable();
    }

    @Override
    protected void configure ( AuthenticationManagerBuilder auth ) throws Exception
    {
        auth.userDetailsService( userService ).passwordEncoder( SecurityConfiguration.passwordEncoder );
    }

    /**
     * Configures a custom AffirmativeBased decision manager that allows us to define a custom RoleHierarchy
     */
    @Bean
    public AffirmativeBased getAccessDecisionManager ()
    {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy( this.produceRoleHierarchy() );

        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler( expressionHandler );

        List<AccessDecisionVoter<?>> voters = new ArrayList<>();

        voters.add( webExpressionVoter );
        return new AffirmativeBased( voters );
    }

    private RoleHierarchy produceRoleHierarchy ()
    {
        //All admins will also have user authorities.
        String roleHierarchySpelString = Authorities.Admin + " > " + Authorities.User;
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy( roleHierarchySpelString );
        return roleHierarchy;
    }

}
