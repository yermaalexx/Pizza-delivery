package com.yermaalexx.pizzadelivery;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DesignPizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IngredientRepository ingredientRepository;

    @BeforeAll
    void setUpRepository() {
        ingredientRepository.deleteAll();
        ingredientRepository.save(new Ingredient("Ham", Ingredient.Type.MEAT));
        ingredientRepository.save(new Ingredient("Tomato", Ingredient.Type.VEGGIES));
        ingredientRepository.save(new Ingredient("Mozzarella", Ingredient.Type.CHEESE));
        ingredientRepository.save(new Ingredient("Tomato sauce", Ingredient.Type.SAUCE));
    }

    @Test
    void testAccessDeniedForUnauthenticatedUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        mockMvc.perform(get("/design"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    void testAccessAllowedForUserRole() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessAllowedForAdminRole() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"));
    }

    @Test
    @WithMockUser
    void testShowDesignForm() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(model().attributeExists("meat", "veggies", "cheese", "sauce", "sizes", "username", "isAdmin"));
    }

    @Test
    @WithMockUser
    void testProcessPizzaValidData() throws Exception {
        mockMvc.perform(post("/design")
                        .param("name", "Test Pizza")
                        .param("ingredients", "Ingredient1")
                        .param("size", "NORMAL")
                        .sessionAttr("pizzaOrder", new PizzaOrder()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/current"));
    }

    @Test
    @WithMockUser
    void testProcessPizzaInvalidData() throws Exception {
        mockMvc.perform(post("/design")
                        .param("name", "")
                        .param("ingredients", "")
                        .param("size", "")
                        .sessionAttr("pizzaOrder", new PizzaOrder()))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(model().attributeHasFieldErrors("pizza", "name", "ingredients", "size"));
    }

}
