package com.yermaalexx.pizzadelivery;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class PizzaDeliveryApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .ignoreIfMissing()
                .systemProperties()
                .load();
        SpringApplication.run(PizzaDeliveryApplication.class, args);
        log.info("Starting application with {} args: {}", args.length, Arrays.toString(args));
    }
}
