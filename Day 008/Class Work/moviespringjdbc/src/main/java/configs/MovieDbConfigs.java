package configs;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MovieDbConfigs {

    @Bean
    public DataSource getDataSource() {

        DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/movieproject");
        ds.setUsername("root");
        ds.setPassword("mohit123");

        return ds;
    }

    @Bean
    public JdbcTemplate getTemplate(DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}