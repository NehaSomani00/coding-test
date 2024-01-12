package com.lumen.fastivr.IVRMLT.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import tollgrade.loopcare.testrequestapi.MLTTESTREQ;
import tollgrade.loopcare.testrequestapi.MLTTESTRSP;

@EnableAsync
@Configuration
public class IvrMltConfig {

	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		return new ThreadPoolTaskExecutor();
	}
	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(MLTTESTREQ.class,MLTTESTRSP.class); // Specify your JAXB annotated classes
		return marshaller;
	}
}
