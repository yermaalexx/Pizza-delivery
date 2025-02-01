package com.yermaalexx.pizzadelivery.repository;

import com.yermaalexx.pizzadelivery.model.UserApp;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<UserApp, UUID>,
        ListCrudRepository<UserApp, UUID> {
    UserApp findByUsername(String username);
    boolean existsByUsername(String username);
}
