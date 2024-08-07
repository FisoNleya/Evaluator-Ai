package com.fiso.nleya.marker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MarkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkerApplication.class, args);
	}

}
