package com.yermaalexx.pizzadelivery.model;

import lombok.Data;
import org.springframework.data.repository.ListCrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public <T> void setPages(ListCrudRepository<T, UUID> repository) {
        this.count = repository.count();
        this.totalPages = (int)this.count/this.itemsOnPage;
        if((int)this.count%this.itemsOnPage!=0
                || this.count==0)
            this.totalPages++;
        if(this.page > this.totalPages)
            this.page = this.totalPages;
        if(this.page <= 0)
            this.page = 1;
    }
}
