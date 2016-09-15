package app.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class CorsConfiguration extends WebMvcConfigurerAdapter
{

    @Override
    public void addCorsMappings ( CorsRegistry registry )
    {
        registry.addMapping( "/**" );
    }
}
