package com.yermaalexx.pizzadelivery.repository;

import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface OrderRepository extends PagingAndSortingRepository<PizzaOrder, UUID> {
    PizzaOrder save(PizzaOrder pizzaOrder);
    void deleteById(UUID id);
    long count();
}
