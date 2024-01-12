package com.lumen.fastivr;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FastivrBackendServiceApplication {

	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(FastivrBackendServiceApplication.class);
		//Initializer for reading the properties from DB
		//app.addInitializers(new ReadDbPropertiesPostProcessor());
		app.run(args);
	}
	
}
