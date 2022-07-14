package com.openclassrooms.new_safety_net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.openclassrooms.new_safety_net.service.LoggerApiNewSafetyNet;

@SpringBootApplication
public class NewSafetyNetApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewSafetyNetApplication.class, args);
		setLogger();
	}

	private static void setLogger() {
		LoggerApiNewSafetyNet.setLogger();
	}

}
