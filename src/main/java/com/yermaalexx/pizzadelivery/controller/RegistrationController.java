package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.model.RegistrationForm;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@Slf4j
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
        log.debug("Showing Register page");
        return "registration";
    }

    @PostMapping
    public String processRegistration(@Valid RegistrationForm form, Errors errors) {
        log.info("Processing registration");
        if(errors.hasErrors() || !form.getPassword().equals(form.getConfirm())) {
            log.warn("Validation errors encountered: {}", errors.getAllErrors());
            return "registration";
        }
        userRepository.save(form.toUser(passwordEncoder));
        log.info("Registration complete for user: {}", form.getUsername());
        return "redirect:/login";
    }
}
