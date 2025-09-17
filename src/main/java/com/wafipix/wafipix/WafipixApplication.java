package com.wafipix.wafipix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WafipixApplication {

	public static void main(String[] args) {
		SpringApplication.run(WafipixApplication.class, args);
	}

}
