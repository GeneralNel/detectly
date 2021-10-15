package com.capital6.detectly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class FileEndpointApplication {

	public static void main(String[] args) {

		SpringApplication.run(FileEndpointApplication.class, args);

	}

}
