package com.lumen.fastivr.IVRConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class IVRConfig {
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	@Bean
	public HttpClient httpClient() {
		return  HttpClient.newBuilder()
				.connectTimeout(Duration.ofMinutes(2L))
				.build();
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
