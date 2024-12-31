package com.yermaalexx.pizzadelivery;

import com.yermaalexx.pizzadelivery.model.RegistrationForm;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
        Mockito.when(userRepository.save(Mockito.any(UserApp.class))).thenReturn(null);
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("confirm", "password123")
                        .param("phone", "1234567890")
                        .param("street", "Main Street")
                        .param("house", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        RegistrationForm form = new RegistrationForm("testuser",
                "password123", "password123", "1234567890",
                "Main Street", "123");
        UserApp userApp = form.toUser(encoder);

        ArgumentCaptor<UserApp> captor = ArgumentCaptor.forClass(UserApp.class);
        verify(userRepository, times(1)).save(captor.capture());

        UserApp savedUser = captor.getValue();
        assertEquals(userApp.getUsername(), savedUser.getUsername());
        assertTrue(encoder.matches("password123", savedUser.getPassword()));
        assertTrue(encoder.matches("password123", userApp.getPassword()));
        assertEquals(userApp.getPhone(), savedUser.getPhone());
        assertEquals(userApp.getStreet(), savedUser.getStreet());
        assertEquals(userApp.getHouse(), savedUser.getHouse());
        assertEquals(userApp.getAuthority(), savedUser.getAuthority());
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
