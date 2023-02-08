package com.openclassrooms.new_safety_net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;

@SpringBootApplication
public class NewSafetyNetApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(NewSafetyNetApplication.class, args);
		setLogger();
	}

	private static void setLogger() {
		LoggerApiNewSafetyNet.setLogger();
	}

	/**
	 * @return HttpExchangeRepository
	 */
	@Bean
	public HttpExchangeRepository httpTraceRepository() {
		return new InMemoryHttpExchangeRepository();
	}

}
