package elan.verify.rmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApp {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApp.class, args);
	}

}
