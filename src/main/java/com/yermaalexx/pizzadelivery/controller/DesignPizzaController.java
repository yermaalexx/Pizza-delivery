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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.stream.Collectors;

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

    @ModelAttribute(name = "username")
    public String user(@AuthenticationPrincipal UserApp user) {
        return (user==null) ? " " : user.getUsername();
    }

    @ModelAttribute(name = "isAdmin")
    public boolean isAdmin(@AuthenticationPrincipal UserApp user) {
        return (user!=null) && user.isAdmin();
    }

    @GetMapping
    public String showDesignForm(@AuthenticationPrincipal UserApp user) {
        String username = (user==null) ? " " : user.getUsername();
        log.debug("Showing pizza design form, username: {}", username);
        return "design";
    }

    @PostMapping
    public String processPizza(@Valid Pizza pizza, Errors errors,
                               @ModelAttribute PizzaOrder pizzaOrder) {
        log.info("Processing pizza: {}", pizza);
        if(errors.hasErrors()) {
            log.warn("Validation errors encountered: {}", errors.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
            return "design";
        }
        pizza.setCreatedAt(new Date());
        pizzaOrder.addPizza(pizza);
        log.info("Pizza added to order: {}", pizza);

        return "redirect:/orders/current";
    }


}
