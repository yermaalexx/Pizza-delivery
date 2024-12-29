package com.yermaalexx.pizzadelivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue
    private UUID id;
    private final String name;
    private final Type type;

    public enum Type {
        MEAT, VEGGIES, CHEESE, SAUCE
    }

}
