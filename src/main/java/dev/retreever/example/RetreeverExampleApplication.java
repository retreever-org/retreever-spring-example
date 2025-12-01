package dev.retreever.example;

import dev.retreever.domain.annotation.ApiDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ApiDoc(
        name = "Retreever Example",
        description = "A Demo Application to demonstrate the working of Retreever"
)
@SpringBootApplication
public class RetreeverExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetreeverExampleApplication.class, args);
	}

}
