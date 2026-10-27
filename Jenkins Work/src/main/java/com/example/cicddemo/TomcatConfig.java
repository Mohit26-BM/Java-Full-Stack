package com.example.cicddemo;

import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

	// Browsers can send more than Tomcat's default 10 Accept-Language entries
	@Bean
	public TomcatConnectorCustomizer maxLocaleCountCustomizer() {
		return connector -> connector.setMaxLocaleCount(50);
	}

}
