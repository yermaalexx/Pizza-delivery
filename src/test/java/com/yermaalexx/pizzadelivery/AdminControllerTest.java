package com.yermaalexx.pizzadelivery;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import com.yermaalexx.pizzadelivery.repository.OrderRepository;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private IngredientRepository ingredientRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @BeforeEach
    void setUpRepository() {
        when(ingredientRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new Ingredient(UUID.randomUUID(), "Meat", Ingredient.Type.MEAT))));
        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new UserApp(UUID.randomUUID(), "admin", encoder.encode("admin"),
                        "-", "-", "-", "ADMIN"))));
        when(orderRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new PizzaOrder(UUID.randomUUID(), new Date(), "-", "-",
                        "-", "-", new ArrayList<>()))));
    }

    @BeforeEach
    void setUpUser() {
        UserApp mockUser = new UserApp(UUID.randomUUID(), "admin", encoder.encode("admin"),
           "-", "-", "-", "ADMIN");
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(mockUser, null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );
    }

    @Test
    void testAccessDeniedForNotAdmin() throws Exception {
        UserApp mockUser = new UserApp("user", encoder.encode("pass"),
                "777-77-77", "Street", "007", "USER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
        mockMvc.perform(get("/v1/admin"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testShowAdminPage() throws Exception {
        mockMvc.perform(get("/v1/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPage"));
    }

    @Test
    void testAddIngredient() throws Exception {
        mockMvc.perform(post("/v1/admin/ingredients/add")
                        .param("ingredientName", "Meat")
                        .param("ingredientType", "MEAT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testDeleteIngredient() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(post("/v1/admin/ingredients/delete")
                        .param("listToRemove", uuid.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        verify(ingredientRepository, times(1)).deleteById(uuid);
    }

    @Test
    void testDeleteUser() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(post("/v1/admin/users/delete")
                        .param("listToRemove", uuid.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        verify(userRepository, times(1)).deleteById(uuid);
    }

    @Test
    void testDeleteOrder() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(post("/v1/admin/orders/delete")
                        .param("listToRemove", uuid.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/v1/admin"));

        verify(orderRepository, times(1)).deleteById(uuid);
    }

}
