package com.company.spring.extension;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.company.spring.config.DbConfig;

/*
initMethod / destroyMethod: lifecycle callbacks declared in the
CONFIGURATION instead of inside the class.
*/
@Configuration
@Import(DbConfig.class)
public class ExtensionConfig {

    @Bean(initMethod = "connect", destroyMethod = "disconnect")
    public LegacyDatabaseClient legacyDatabaseClient(DataSource dataSource) {
        return new LegacyDatabaseClient(dataSource);
    }

    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    public AllCallbacksBean allCallbacksBean() {
        return new AllCallbacksBean();
    }
}
