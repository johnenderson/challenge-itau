package com.itau.pixms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PixMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PixMsApplication.class, args);
	}

}
