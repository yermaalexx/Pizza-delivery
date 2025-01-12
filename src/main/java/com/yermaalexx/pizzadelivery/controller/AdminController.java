package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.config.AppConfig;
import com.yermaalexx.pizzadelivery.model.Ingredient;
import com.yermaalexx.pizzadelivery.model.ObjectsToDisplay;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.IngredientRepository;
import com.yermaalexx.pizzadelivery.repository.OrderRepository;
import com.yermaalexx.pizzadelivery.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Controller
@RequestMapping("/v1/admin")
@Slf4j
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
                Math.max(ingredientsToDisplay.getItemsOnPage(), 1),
                Sort.by("type", "name").ascending());
        model.addAttribute("allIngredients",
                ingredientRepository.findAll(pageRequest).getContent());
        log.trace("Ingredients added to model");
        return ingredientsToDisplay;
    }

    @ModelAttribute(name = "usersToDisplay")
    public ObjectsToDisplay usersDisplay(Model model) {
        usersToDisplay.setPages(userRepository);

        PageRequest pageRequest = PageRequest.of(usersToDisplay.getPage()-1,
                Math.max(usersToDisplay.getItemsOnPage(), 1),
                Sort.by("authority").ascending());
        model.addAttribute("allUsers",
                userRepository.findAll(pageRequest).getContent());
        log.trace("Users added to model");
        return usersToDisplay;
    }

    @ModelAttribute(name = "ordersToDisplay")
    public ObjectsToDisplay ordersDisplay(Model model) {
        ordersToDisplay.setPages(orderRepository);

        PageRequest pageRequest = PageRequest.of(ordersToDisplay.getPage()-1,
                Math.max(ordersToDisplay.getItemsOnPage(), 1),
                Sort.by("placedAt").descending());
        model.addAttribute("allOrders",
                orderRepository.findAll(pageRequest).getContent());
        log.trace("Orders added to model");
        return ordersToDisplay;
    }

    @ModelAttribute(name = "username")
    public String user(@AuthenticationPrincipal UserApp user) {
        return (user==null) ? " " : user.getUsername();
    }

    @ModelAttribute(name = "ingredientTypes")
    public Ingredient.Type[] ingredientTypes() {
        return Ingredient.Type.values();
    }

    @GetMapping
    public String showAdminPage() {
        log.trace("Current user's roles: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        log.debug("Showing Admin page");
        return "adminPage";
    }

    @PostMapping("/ingredients/delete")
    public String deleteIngredient(@ModelAttribute ObjectsToDisplay ingredientsToDisplay) {
        log.info("Processing ingredients deleting: {}", Arrays.toString(ingredientsToDisplay.getListToRemove().toArray()));
        int deleted = 0;
        for(String str : ingredientsToDisplay.getListToRemove()) {
            UUID id = UUID.fromString(str);
            ingredientRepository.deleteById(id);
            deleted++;
        }
        this.ingredientsToDisplay.setPage(ingredientsToDisplay.getPage());
        this.ingredientsToDisplay.setListToRemove(new ArrayList<>());
        log.info("Ingredients deleted: {}", deleted);
        return "redirect:/v1/admin";
    }

    @PostMapping("/ingredients/add")
    public String addIngredient(@ModelAttribute ObjectsToDisplay ingredientsToDisplay) {
        log.info("Processing ingredient adding: {}, type: {}", ingredientsToDisplay.getIngredientName(), ingredientsToDisplay.getIngredientType());
        if(ingredientsToDisplay.getIngredientName()!=null
                && !ingredientsToDisplay.getIngredientName().isBlank()
                && ingredientsToDisplay.getIngredientType()!=null) {
            Ingredient ingredient = new Ingredient(ingredientsToDisplay.getIngredientName(),
                    ingredientsToDisplay.getIngredientType());
            ingredientRepository.save(ingredient);
            log.info("Ingredients added successfully: {}, type: {}", ingredientsToDisplay.getIngredientName(), ingredientsToDisplay.getIngredientType());
        }
        ingredientsToDisplay.setIngredientName(null);
        ingredientsToDisplay.setIngredientType(null);

        return "redirect:/v1/admin";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@ModelAttribute ObjectsToDisplay usersToDisplay) {
        log.info("Processing users deleting: {}", Arrays.toString(usersToDisplay.getListToRemove().toArray()));
        int deleted = 0;
        for(String str : usersToDisplay.getListToRemove()) {
            UUID id = UUID.fromString(str);
            if(userRepository.findById(id).isEmpty() || userRepository.findById(id).get().isAdmin())
                continue;
            userRepository.deleteById(id);
            deleted++;
        }
        this.usersToDisplay.setPage(usersToDisplay.getPage());
        this.usersToDisplay.setListToRemove(new ArrayList<>());
        log.info("Users deleted: {}", deleted);
        return "redirect:/v1/admin";
    }

    @PostMapping("/orders/delete")
    public String deleteOrder(@ModelAttribute ObjectsToDisplay ordersToDisplay) {
        log.info("Processing orders deleting: {}", Arrays.toString(ordersToDisplay.getListToRemove().toArray()));
        int deleted = 0;
        for(String str : ordersToDisplay.getListToRemove()) {
            UUID id = UUID.fromString(str);
            orderRepository.deleteById(id);
            deleted++;
        }
        this.ordersToDisplay.setPage(ordersToDisplay.getPage());
        this.ordersToDisplay.setListToRemove(new ArrayList<>());
        log.info("Orders deleted: {}", deleted);
        return "redirect:/v1/admin";
    }

}
