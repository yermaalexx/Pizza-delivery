package com.yermaalexx.pizzadelivery.security;

import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return username -> {
            UserApp user = repository.findByUsername(username);
            if(user != null)
                return user;
            throw new UsernameNotFoundException("UserApp '" + username + "' not found");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureAuthorization(http);
        configureFormLogin(http);
        configureCsrf(http);
        configureHeaders(http);
        configureLogout(http);

        return http.build();
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/design", "/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll());
    }

    private void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(formLoginConfigurer -> formLoginConfigurer
                .loginPage("/login")
                .defaultSuccessUrl("/design", true));
    }

    private void configureCsrf(HttpSecurity http) throws Exception {
        http.csrf(csrfConfigurer -> csrfConfigurer.disable());
    }

    private void configureHeaders(HttpSecurity http) throws Exception {
        http.headers(headersConfigurer -> headersConfigurer
                .frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()));
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logoutConfigurer -> logoutConfigurer.logoutSuccessUrl("/"));
    }

}
