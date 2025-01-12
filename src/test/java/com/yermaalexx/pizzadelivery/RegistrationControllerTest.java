package com.yermaalexx.pizzadelivery;

import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    void testGetRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("registrationForm"));
    }

    @Test
    void testProcessRegistrationWithValidForm() throws Exception {
        userRepository.deleteAll();
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("confirm", "password123")
                        .param("phone", "1234567890")
                        .param("street", "Main Street")
                        .param("house", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        UserApp user = userRepository.findByUsername("testuser");
        assertTrue(encoder.matches("password123", user.getPassword()));
        assertEquals("1234567890", user.getPhone());
        assertEquals("Main Street", user.getStreet());
        assertEquals("123", user.getHouse());

    }

    @Test
    void testProcessRegistrationWithValidationErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", "password123")
                        .param("confirm", "differentpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "username"))
                .andExpect(model().attributeHasErrors("registrationForm"));
    }

}
