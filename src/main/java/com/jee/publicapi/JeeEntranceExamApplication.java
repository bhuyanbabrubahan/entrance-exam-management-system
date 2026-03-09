package com.jee.publicapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.jee.publicapi.config.DocumentStorageProperties;

@SpringBootApplication
//@EnableConfigurationProperties(DocumentStorageProperties.class)
@EnableScheduling   // ⭐ ADD THIS
public class JeeEntranceExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(JeeEntranceExamApplication.class, args);
		System.out.println("Application running...");
		System.out.println(System.getProperty("DB_USERNAME"));
		System.out.println(System.getenv("DB_USERNAME"));
		
	}
}
