package com.needbackend_app.needapp;

import com.needbackend_app.needapp.auth.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {ConfigProperties.class})
public class NeedappApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeedappApplication.class, args);
	}

}
