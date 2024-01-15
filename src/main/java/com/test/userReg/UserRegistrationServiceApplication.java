package com.test.userReg;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class UserRegistrationServiceApplication {

	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(UserRegistrationServiceApplication.class);

		app.run(args);
	}
	
}
