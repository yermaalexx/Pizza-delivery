package com.yermaalexx.pizzadelivery.repository;

import com.yermaalexx.pizzadelivery.model.UserApp;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<UserApp, UUID> {
    UserApp findByUsername(String username);
    void deleteById(UUID id);
    UserApp save(UserApp userApp);
    long count();
}
