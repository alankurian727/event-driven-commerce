package com.alankurian.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventDrivenCommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventDrivenCommerceApplication.class, args);
	}

}
