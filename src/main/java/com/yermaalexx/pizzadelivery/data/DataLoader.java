package com.yermaalexx.pizzadelivery.data;

import com.yermaalexx.pizzadelivery.config.AppConfig;
import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner dataLoaderForIngredient(IngredientRepository ingredientRepository,
                                                     UserRepository userRepository,
                                                     PasswordEncoder encoder,
                                                     AppConfig appConfig) {
        return (args) -> {
            if(ingredientRepository.count() == 0) {
                ingredientRepository.save(new Ingredient("Chicken", Ingredient.Type.MEAT));
                ingredientRepository.save(new Ingredient("Ham", Ingredient.Type.MEAT));
                ingredientRepository.save(new Ingredient("Sausages", Ingredient.Type.MEAT));
                ingredientRepository.save(new Ingredient("Tomato", Ingredient.Type.VEGGIES));
                ingredientRepository.save(new Ingredient("Olives", Ingredient.Type.VEGGIES));
                ingredientRepository.save(new Ingredient("Bell pepper", Ingredient.Type.VEGGIES));
                ingredientRepository.save(new Ingredient("Mozzarella", Ingredient.Type.CHEESE));
                ingredientRepository.save(new Ingredient("Parmesan", Ingredient.Type.CHEESE));
                ingredientRepository.save(new Ingredient("Philadelphia", Ingredient.Type.CHEESE));
                ingredientRepository.save(new Ingredient("Tomato sauce", Ingredient.Type.SAUCE));
                ingredientRepository.save(new Ingredient("BBQ sauce", Ingredient.Type.SAUCE));
                ingredientRepository.save(new Ingredient("Caesar sauce", Ingredient.Type.SAUCE));
            }
            if(userRepository.count() == 0) {
                userRepository.save(new UserApp(appConfig.getAdminName()
                        , encoder.encode(appConfig.getAdminPass()),
                            "-", "-", "-", "ADMIN"));
            }
        };
    }
}
