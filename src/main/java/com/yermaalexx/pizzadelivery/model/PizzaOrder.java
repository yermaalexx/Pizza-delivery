package com.yermaalexx.pizzadelivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
@Entity
public class PizzaOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Date placedAt = new Date();

    @NotBlank(message = "Name is required")
    private String deliveryName;
    
    @NotBlank(message = "Phone is required")
    private String deliveryPhone;

    @NotBlank(message = "Delivery street is required")
    private String deliveryStreet;

    @NotBlank(message = "Delivery house is required")
    private String deliveryHouse;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Pizza> pizzas = new ArrayList<>();

    public void addPizza(Pizza pizza) {
        this.pizzas.add(pizza);
    }

    public void removePizzasByDate(List<String> listToRemove) {
        pizzas.removeIf(pizza -> listToRemove.contains(pizza.getCreatedAt().toString()));
    }

    public boolean hasNoPizzas() {
        return (pizzas.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("name: "+deliveryName+", phone: "+deliveryPhone
                +", street: "+deliveryStreet+", house: "+deliveryHouse+", pizzas ("+pizzas.size()+"): ");
        if(pizzas != null) {
            for(Pizza pizza : pizzas) {
                str.append(pizza.getId());
                str.append(", ");
            }
            str.replace(str.length()-2, str.length(), ";");
        }

        return str.toString();

    }

}
