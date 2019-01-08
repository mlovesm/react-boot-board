package com.example.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.boot.file.FileStorageProperties;

@SpringBootApplication
public class SecurityBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityBoardApplication.class, args);
	}
}
