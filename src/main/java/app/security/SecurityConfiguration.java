package app.security;

import app.repositories.UserRepository;
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
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void configure ( HttpSecurity http ) throws Exception
    {
        http.authorizeRequests()
                .accessDecisionManager( this.getAccessDecisionManager() )
                .antMatchers( "/*" ).hasAuthority( Authorities.USER.toString() )
                .and()
                .formLogin();
    }

    @Override
    protected void configure ( AuthenticationManagerBuilder auth ) throws Exception
    {
        auth.userDetailsService( username -> userRepository.findByUsername( username ).orElseThrow( RuntimeException::new ) )
                .passwordEncoder( new BCryptPasswordEncoder() );
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

        List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();

        voters.add( webExpressionVoter );
        return new AffirmativeBased( voters );
    }

    private RoleHierarchy produceRoleHierarchy ()
    {
        String roleHierarchySpelString = Authorities.ADMIN + " > " + Authorities.USER;
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy( roleHierarchySpelString );
        return roleHierarchy;
    }
}
