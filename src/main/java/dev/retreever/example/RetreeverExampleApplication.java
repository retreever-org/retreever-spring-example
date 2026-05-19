package dev.retreever.example;

import dev.retreever.annotation.ApiDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@ApiDoc(
        name = "E-Commerce API",
        description = "A Demo Application to demonstrate the working of Retreever"
)
@SpringBootApplication
@EnableScheduling
public class RetreeverExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetreeverExampleApplication.class, args);
	}

}
