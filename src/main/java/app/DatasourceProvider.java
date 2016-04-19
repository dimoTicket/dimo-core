package app;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DatasourceProvider
{

    @Value ( "${spring.datasource.driver-class-name}" )
    private String databaseDriverClassName;
    @Value ( "${spring.datasource.url}" )
    private String datasourceUrl;
    @Value ( "${spring.datasource.username}" )
    private String databaseUsername;
    @Value ( "${spring.datasource.password}" )
    private String databasePassword;

    public DataSource datasource ()
    {
        org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
        datasource.setDriverClassName( databaseDriverClassName );
        datasource.setUrl( datasourceUrl );
        datasource.setUsername( databaseUsername );
        datasource.setPassword( databasePassword );
        return datasource;
    }
}
