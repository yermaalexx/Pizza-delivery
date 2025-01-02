package com.yermaalexx.pizzadelivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Pizza {

    @Id
    @GeneratedValue
    private UUID id;
    @NotNull
    @Size(min = 3, message = "Name must be at least 3 characters long")
    private String name;

    private Date createdAt = new Date();
    @NotNull(message = "You must choose size of your pizza")
    private SizeOfPizza size;
    @NotNull
    @Size(min = 1, message = "You must choose at least 1 ingredient")
    @ElementCollection
    private List<String> ingredients;

    public enum SizeOfPizza {
        MINI, NORMAL, MAX
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("name: "+name+", size: "+size+", ingredients: ");
        if(ingredients != null) {
            for(String ingredient : ingredients) {
                str.append(ingredient);
                str.append(", ");
            }
            str.replace(str.length()-2, str.length(), ";");
        }

        return str.toString();

    }
}
