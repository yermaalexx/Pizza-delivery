package com.yermaalexx.pizzadelivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class PizzaDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PizzaDeliveryApplication.class, args);
        log.info("Starting application with {} args: {}", args.length, Arrays.toString(args));
    }
}
