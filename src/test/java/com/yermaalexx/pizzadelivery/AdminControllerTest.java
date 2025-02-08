package com.yermaalexx.pizzadelivery;


import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import com.yermaalexx.pizzadelivery.repository.OrderRepository;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    void setUpRepository() {
        ingredientRepository.deleteAll();
        userRepository.deleteAll();
        orderRepository.deleteAll();
        ingredientRepository.save(new Ingredient("Ham", Ingredient.Type.MEAT));
        ingredientRepository.save(new Ingredient("Tomato", Ingredient.Type.VEGGIES));
        ingredientRepository.save(new Ingredient("Mozzarella", Ingredient.Type.CHEESE));
        userRepository.save(new UserApp("user", encoder.encode("pass"),
                "777-77-77", "Street", "007", "USER"));
        userRepository.save(new UserApp("admin", encoder.encode("admin"),
                "-", "-", "-", "ADMIN"));
        orderRepository.save(new PizzaOrder(null, new Date(), "-", "-", "-", "-", new ArrayList<>()));
    }

    @Test
    @WithMockUser
    void testAccessDeniedForNotAdmin() throws Exception {
        mockMvc.perform(get("/v1/admin"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowAdminPage() throws Exception {
        mockMvc.perform(get("/v1/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPage"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddIngredient() throws Exception {
        long ingredientsBefore = ingredientRepository.count();
        mockMvc.perform(post("/v1/admin/ingredients/add")
                        .param("ingredientName", "Meat")
                        .param("ingredientType", "MEAT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        assertEquals(ingredientRepository.count(), ingredientsBefore+1);
        assertTrue(ingredientRepository.existsByName("Meat"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteIngredient() throws Exception {
        UUID uuid = ingredientRepository.findAll().get(0).getId();
        long ingredientsBefore = ingredientRepository.count();
        mockMvc.perform(post("/v1/admin/ingredients/delete")
                        .param("listToRemove", uuid.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        assertEquals(ingredientRepository.count(), ingredientsBefore-1);
        assertFalse(ingredientRepository.existsById(uuid));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        UserApp user = userRepository.findByUsername("user");
        UUID uuidUser = user.getId();
        UUID uuidAdmin = userRepository.findByUsername("admin").getId();
        long usersBefore = userRepository.count();

        mockMvc.perform(post("/v1/admin/users/delete")
                        .param("listToRemove", uuidUser.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        assertEquals(userRepository.count(), usersBefore-1);
        assertFalse(userRepository.existsById(uuidUser));

        mockMvc.perform(post("/v1/admin/users/delete")
                        .param("listToRemove", uuidAdmin.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        assertTrue(userRepository.existsById(uuidAdmin));

        user.setId(null);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteOrder() throws Exception {
        UUID uuid = orderRepository.findAll().get(0).getId();
        long ordersBefore = orderRepository.count();
        mockMvc.perform(post("/v1/admin/orders/delete")
                        .param("listToRemove", uuid.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        assertEquals(orderRepository.count(), ordersBefore-1);
        assertFalse(orderRepository.existsById(uuid));
    }

}
