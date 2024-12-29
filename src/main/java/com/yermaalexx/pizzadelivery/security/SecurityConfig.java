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
        http
                .authorizeHttpRequests((autorize) -> autorize
                        .requestMatchers("/design", "/orders/**")
                            .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/v1/admin/**")
                            .hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin((formLoginConfigurer) -> formLoginConfigurer
                        .loginPage("/login")
                        .defaultSuccessUrl("/design", true))
                .csrf((csrfConfigurer) -> csrfConfigurer
                        .disable())
                        //.ignoringRequestMatchers("/h2-console/**"))
                .headers((headersConfigurer) -> headersConfigurer
                        .frameOptions((frameOptionsConfig) -> frameOptionsConfig.sameOrigin()))
                .logout((logoutConfigurer) -> logoutConfigurer
                        .logoutSuccessUrl("/"));

        return http.build();
    }
}
