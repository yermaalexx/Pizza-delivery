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

    public ObjectsToDisplay(int itemsOnPage) {
        this.listToRemove = new ArrayList<>();
        this.count = 0;
        this.itemsOnPage = Math.max(3, itemsOnPage);
        this.page = 1;
        this.totalPages = 0;
    }

    public ObjectsToDisplay() {
        this.listToRemove = new ArrayList<>();
        this.count = 0;
        this.itemsOnPage = 10;
        this.page = 1;
        this.totalPages = 0;
    }

    public <T> void setPages(ListCrudRepository<T, UUID> repository) {
        count = repository.count();
        totalPages = (itemsOnPage==0) ? 1 : (int)count/itemsOnPage;
        if((itemsOnPage!=0) && ((int)count%itemsOnPage!=0 || count==0))
            totalPages++;
        if(page > totalPages)
            page = totalPages;
        if(page <= 0)
            page = 1;
    }
}
