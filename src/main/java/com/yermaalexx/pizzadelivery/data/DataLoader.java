package com.yermaalexx.pizzadelivery.data;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("!prod")
@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner dataLoaderForIngredient(IngredientRepository repo,
                                                     UserRepository userRepository,
                                                     PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                repo.save(new Ingredient("Chicken", Ingredient.Type.MEAT));
                repo.save(new Ingredient("Ham", Ingredient.Type.MEAT));
                repo.save(new Ingredient("Sausages", Ingredient.Type.MEAT));
                repo.save(new Ingredient("Tomato", Ingredient.Type.VEGGIES));
                repo.save(new Ingredient("Olives", Ingredient.Type.VEGGIES));
                repo.save(new Ingredient("Bell pepper", Ingredient.Type.VEGGIES));
                repo.save(new Ingredient("Mozzarella", Ingredient.Type.CHEESE));
                repo.save(new Ingredient("Parmesan", Ingredient.Type.CHEESE));
                repo.save(new Ingredient("Philadelphia", Ingredient.Type.CHEESE));
                repo.save(new Ingredient("Tomato sauce", Ingredient.Type.SAUCE));
                repo.save(new Ingredient("BBQ sauce", Ingredient.Type.SAUCE));
                repo.save(new Ingredient("Caesar sauce", Ingredient.Type.SAUCE));
                userRepository.save(new UserApp("user", encoder.encode("pass"),
                        "777-77-77", "Street", "007",
                        "USER"));
                userRepository.save(new UserApp("admin", encoder.encode("admin"),
                        "-", "-", "-",
                        "ADMIN"));
            }
        };
    }
}
