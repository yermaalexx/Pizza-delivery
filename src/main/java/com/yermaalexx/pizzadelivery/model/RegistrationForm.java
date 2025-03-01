package com.yermaalexx.pizzadelivery.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationForm {
    @NotBlank(message = "Name is required")
    private String username;
    @NotBlank(message = "Not blanc password is required")
    private String password;
    @NotBlank(message = "Not blanc confirm is required")
    private String confirm;
    @NotBlank(message = "Phone is required")
    private String phone;
    @NotBlank(message = "Street is required")
    private String street;
    @NotBlank(message = "House is required")
    private String house;

    public UserApp toUser(PasswordEncoder passwordEncoder) {
        return new UserApp(username, passwordEncoder.encode(password), phone, street, house,
                "USER");
    }
}
