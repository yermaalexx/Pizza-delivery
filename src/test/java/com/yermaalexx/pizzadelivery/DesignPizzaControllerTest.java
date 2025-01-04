package com.yermaalexx.pizzadelivery;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DesignPizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void setUpRepository() {
        when(ingredientRepository.findAllByType(any(Ingredient.Type.class)))
                .thenReturn(List.of(new Ingredient(UUID.fromString("8e3f2dbb-b3d6-4146-b587-38ab90ca16ca"), "Ingredient1", Ingredient.Type.CHEESE)));
    }

    @BeforeEach
    void setUpUser() {
        UserApp mockUser = new UserApp("user", encoder.encode("pass"),
                "777-77-77", "Street", "007",
                "USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
    }

    @Test
    void testAccessDeniedForUnauthenticatedUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        mockMvc.perform(get("/design"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void testAccessAllowedForUserRole() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"));
    }

    @Test
    void testAccessAllowedForAdminRole() throws Exception {
        UserApp mockUser = new UserApp("admin", encoder.encode("admin"),
                "-", "-", "-", "ADMIN");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"));
    }

    @Test
    void testShowDesignForm() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(model().attributeExists("meat", "veggies", "cheese", "sauce", "sizes", "user"));
    }

    @Test
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
