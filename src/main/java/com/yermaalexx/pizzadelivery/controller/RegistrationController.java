package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.model.RegistrationForm;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute(name = "registrationForm")
    public RegistrationForm form() {
        return new RegistrationForm();
    }

    @GetMapping
    public String registerForm() {
        return "registration";
    }

    @PostMapping
    public String processRegistration(@Valid RegistrationForm form, Errors errors) {
        if(errors.hasErrors() || !form.getPassword().equals(form.getConfirm())) {
            return "registration";
        }

        userRepository.save(form.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
