package org.springy.som.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.Modulithic;

import org.springy.som.security.KeycloakTokenClient;
import org.springy.som.security.KeycloakTokenRefresher;

@Import({KeycloakTokenClient.class, KeycloakTokenRefresher.class})
@Modulithic
@SpringBootApplication
public class SomServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SomServerApplication.class, args);
	}

}
