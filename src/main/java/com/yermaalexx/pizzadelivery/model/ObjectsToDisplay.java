package com.yermaalexx.pizzadelivery.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ObjectsToDisplay {
    private List<String> listToRemove;
    private long count;
    private int itemsOnPage;
    private int page;
    private int totalPages;
    private String ingredientName;
    private Ingredient.Type ingredientType;

    public ObjectsToDisplay() {
        this.listToRemove = new ArrayList<>();
        this.count = 0;
        this.itemsOnPage = 5;
        this.page = 1;
        this.totalPages = 0;
    }
}
