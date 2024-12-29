package com.yermaalexx.pizzadelivery.repository;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, UUID>,
        ListCrudRepository<Ingredient, UUID> {
    List<Ingredient> findAllByType(Ingredient.Type type);
    List<Ingredient> findAll();
    String findNameById(UUID id);

}
