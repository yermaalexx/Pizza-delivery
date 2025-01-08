package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.Pizza;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/design")
@SessionAttributes("pizzaOrder")
@Slf4j
public class DesignPizzaController {

    private final IngredientRepository ingredientRepository;

    public DesignPizzaController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        Ingredient.Type[] types = Ingredient.Type.values();
        for(Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    ingredientRepository.findAllByType(type));
        }
    }

    @ModelAttribute(name = "pizzaOrder")
    public PizzaOrder order() {
        return new PizzaOrder();
    }

    @ModelAttribute(name = "pizza")
    public Pizza pizza() { return new Pizza(); }

    @ModelAttribute(name = "sizes")
    public Pizza.SizeOfPizza[] sizes() {
        return Pizza.SizeOfPizza.values();
    }

    @ModelAttribute(name = "user")
    public UserApp user(@AuthenticationPrincipal UserApp user) {
        return user;
    }

    @GetMapping
    public String showDesignForm(@AuthenticationPrincipal UserApp user) {
        log.debug("Showing pizza design form, username: {}", user.getUsername());
        return "design";
    }

    @PostMapping
    public String processPizza(@Valid Pizza pizza, Errors errors,
                               @ModelAttribute PizzaOrder pizzaOrder) {
        log.info("Processing pizza: {}", pizza);
        if(errors.hasErrors()) {
            log.warn("Validation errors encountered: {}", errors.getAllErrors());
            return "design";
        }
        pizza.setCreatedAt(new Date());
        pizzaOrder.addPizza(pizza);
        log.info("Pizza added to order: {}", pizza);

        return "redirect:/orders/current";
    }


}
