package com.example.demo;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
	    exclude = UserDetailsServiceAutoConfiguration.class
		)
public class ServiceConnectBackendApp1Application {

	public static void main(String[] args) {
		SpringApplication.run(ServiceConnectBackendApp1Application.class, args);
		System.out.println("hi"); 
	}

}
