package org.springy.som.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@Modulithic
@SpringBootApplication
public class SomServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SomServerApplication.class, args);
	}

}
