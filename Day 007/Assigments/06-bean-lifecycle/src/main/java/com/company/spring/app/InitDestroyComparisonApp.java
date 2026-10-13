package com.company.spring.app;

import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.company.spring.extension.AllCallbacksBean;
import com.company.spring.extension.ExtensionConfig;
import com.company.spring.extension.LegacyDatabaseClient;

/*
OPTIONAL EXTENSION: @Bean(initMethod, destroyMethod) compared with
@PostConstruct / @PreDestroy and the Spring interfaces.
*/
public class InitDestroyComparisonApp {

    public static void main(String[] args) throws SQLException {

        System.out.println("main: creating the Spring context...");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ExtensionConfig.class);

        System.out.println("main: context started\n");

        context.getBean(LegacyDatabaseClient.class).ping();
        context.getBean(AllCallbacksBean.class).doWork();

        System.out.println("\nmain: calling context.close() explicitly...");
        context.close();
        System.out.println("main: context closed");
    }
}
