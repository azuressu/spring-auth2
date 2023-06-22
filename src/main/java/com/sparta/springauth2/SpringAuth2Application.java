package com.sparta.springauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication // (exclude = SecurityAutoConfiguration.class) // Spring Security 인증 기능 제외
public class SpringAuth2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringAuth2Application.class, args);
	}

}
