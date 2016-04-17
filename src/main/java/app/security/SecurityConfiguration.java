package app.security;

import app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


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
                .antMatchers( "/*" ).authenticated()
                .and()
                .formLogin();
    }

    @Override
    protected void configure ( AuthenticationManagerBuilder auth ) throws Exception
    {
        auth.userDetailsService( username -> userRepository.findByUsername( username ).orElseThrow( RuntimeException::new ) )
                .passwordEncoder( new BCryptPasswordEncoder() );
    }
}
