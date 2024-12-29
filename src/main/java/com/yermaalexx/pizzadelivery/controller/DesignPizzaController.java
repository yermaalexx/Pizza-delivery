package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.Pizza;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/design")
@SessionAttributes("pizzaOrder")
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
    public String showDesignForm() {
        return "design";
    }

    @PostMapping
    public String processPizza(@Valid Pizza pizza, Errors errors,
                               @ModelAttribute PizzaOrder pizzaOrder) {
        if(errors.hasErrors()) {
            return "design";
        }
        pizza.setCreatedAt(new Date());
        pizzaOrder.addPizza(pizza);

        return "redirect:/orders/current";
    }


}
