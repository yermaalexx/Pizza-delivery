package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.config.AppConfig;
import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.ObjectsToDisplay;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import com.yermaalexx.pizzadelivery.repository.OrderRepository;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/v1/admin")
public class AdminController {

    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ObjectsToDisplay ingredientsToDisplay;
    private final ObjectsToDisplay usersToDisplay;
    private final ObjectsToDisplay ordersToDisplay;

    public AdminController(IngredientRepository ingredientRepository,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           AppConfig appConfig) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        int itemsOnPage = appConfig.getItemsOnPage();
        this.ingredientsToDisplay = new ObjectsToDisplay(itemsOnPage);
        this.usersToDisplay = new ObjectsToDisplay(itemsOnPage);
        this.ordersToDisplay = new ObjectsToDisplay(itemsOnPage);
    }

    @ModelAttribute(name = "ingredientsToDisplay")
    public ObjectsToDisplay ingredientsDisplay(Model model) {
        ingredientsToDisplay.setPages(ingredientRepository);

        PageRequest pageRequest = PageRequest.of(ingredientsToDisplay.getPage()-1,
                ingredientsToDisplay.getItemsOnPage(),
                Sort.by("type", "name").ascending());
        model.addAttribute("allIngredients",
                ingredientRepository.findAll(pageRequest).getContent());

        return ingredientsToDisplay;
    }

    @ModelAttribute(name = "usersToDisplay")
    public ObjectsToDisplay usersDisplay(Model model) {
        usersToDisplay.setPages(userRepository);

        PageRequest pageRequest = PageRequest.of(usersToDisplay.getPage()-1,
                usersToDisplay.getItemsOnPage(),
                Sort.by("authority").ascending());
        model.addAttribute("allUsers",
                userRepository.findAll(pageRequest).getContent());

        return usersToDisplay;
    }

    @ModelAttribute(name = "ordersToDisplay")
    public ObjectsToDisplay ordersDisplay(Model model) {
        ordersToDisplay.setPages(orderRepository);

        PageRequest pageRequest = PageRequest.of(ordersToDisplay.getPage()-1,
                ordersToDisplay.getItemsOnPage(),
                Sort.by("placedAt").descending());
        model.addAttribute("allOrders",
                orderRepository.findAll(pageRequest).getContent());

        return ordersToDisplay;
    }

    @ModelAttribute(name = "user")
    public UserApp user(@AuthenticationPrincipal UserApp user) {
        return user;
    }

    @ModelAttribute(name = "ingredientTypes")
    public Ingredient.Type[] ingredientTypes() {
        return Ingredient.Type.values();
    }

    @GetMapping
    public String showAdminPage() {
        return "adminPage";
    }

    @PostMapping("/ingredients/delete")
    public String deleteIngredient(@ModelAttribute ObjectsToDisplay ingredientsToDisplay) {
        for(String str : ingredientsToDisplay.getListToRemove()) {
            UUID id = UUID.fromString(str);
            ingredientRepository.deleteById(id);
        }
        this.ingredientsToDisplay.setPage(ingredientsToDisplay.getPage());
        this.ingredientsToDisplay.setListToRemove(new ArrayList<>());

        return "redirect:/v1/admin";
    }

    @PostMapping("/ingredients/add")
    public String addIngredient(@ModelAttribute ObjectsToDisplay ingredientsToDisplay) {
        if(ingredientsToDisplay.getIngredientName()!=null
                && !ingredientsToDisplay.getIngredientName().isBlank()
                && ingredientsToDisplay.getIngredientType()!=null) {
            Ingredient ingredient = new Ingredient(ingredientsToDisplay.getIngredientName(),
                    ingredientsToDisplay.getIngredientType());
            ingredientRepository.save(ingredient);
        }
        ingredientsToDisplay.setIngredientName(null);
        ingredientsToDisplay.setIngredientType(null);

        return "redirect:/v1/admin";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@ModelAttribute ObjectsToDisplay usersToDisplay,
                             @AuthenticationPrincipal UserApp user) {
        UUID currentId = user.getId();
        for(String str : usersToDisplay.getListToRemove()) {
            UUID id = UUID.fromString(str);
            if(currentId.equals(id))
                continue;
            userRepository.deleteById(id);
        }
        this.usersToDisplay.setPage(usersToDisplay.getPage());
        this.usersToDisplay.setListToRemove(new ArrayList<>());

        return "redirect:/v1/admin";
    }

    @PostMapping("/orders/delete")
    public String deleteOrder(@ModelAttribute ObjectsToDisplay ordersToDisplay) {
        for(String str : ordersToDisplay.getListToRemove()) {
            UUID id = UUID.fromString(str);
            orderRepository.deleteById(id);
        }
        this.ordersToDisplay.setPage(ordersToDisplay.getPage());
        this.ordersToDisplay.setListToRemove(new ArrayList<>());

        return "redirect:/v1/admin";
    }

}
