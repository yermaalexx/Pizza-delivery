package com.yermaalexx.pizzadelivery.controller;

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
                           OrderRepository orderRepository) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.ingredientsToDisplay = new ObjectsToDisplay();
        this.usersToDisplay = new ObjectsToDisplay();
        this.ordersToDisplay = new ObjectsToDisplay();
    }

    @ModelAttribute(name = "ingredientsToDisplay")
    public ObjectsToDisplay ingredientsDisplay(Model model) {
        ingredientsToDisplay.setCount(ingredientRepository.count());
        int totalPages = (int)ingredientsToDisplay.getCount()/ingredientsToDisplay.getItemsOnPage();
        if((int)ingredientsToDisplay.getCount()%ingredientsToDisplay.getItemsOnPage()!=0
            || ingredientsToDisplay.getCount()==0)
            totalPages++;
        ingredientsToDisplay.setTotalPages(totalPages);
        if(ingredientsToDisplay.getPage() > ingredientsToDisplay.getTotalPages())
            ingredientsToDisplay.setPage(ingredientsToDisplay.getTotalPages());

        PageRequest pageRequest = PageRequest.of(ingredientsToDisplay.getPage()-1,
                ingredientsToDisplay.getItemsOnPage(),
                Sort.by("type", "name").ascending());
        model.addAttribute("allIngredients",
                ingredientRepository.findAll(pageRequest).getContent());

        return ingredientsToDisplay;
    }

    @ModelAttribute(name = "usersToDisplay")
    public ObjectsToDisplay usersDisplay(Model model) {
        usersToDisplay.setCount(userRepository.count());
        int totalPages = (int)usersToDisplay.getCount()/usersToDisplay.getItemsOnPage();
        if((int)usersToDisplay.getCount()%usersToDisplay.getItemsOnPage()!=0
                || usersToDisplay.getCount()==0)
            totalPages++;
        usersToDisplay.setTotalPages(totalPages);
        if(usersToDisplay.getPage() > usersToDisplay.getTotalPages())
            usersToDisplay.setPage(usersToDisplay.getTotalPages());

        PageRequest pageRequest = PageRequest.of(usersToDisplay.getPage()-1,
                usersToDisplay.getItemsOnPage(),
                Sort.by("authority").ascending());
        model.addAttribute("allUsers",
                userRepository.findAll(pageRequest).getContent());

        return usersToDisplay;
    }

    @ModelAttribute(name = "ordersToDisplay")
    public ObjectsToDisplay ordersDisplay(Model model) {
        ordersToDisplay.setCount(orderRepository.count());
        int totalPages = (int)ordersToDisplay.getCount()/ordersToDisplay.getItemsOnPage();
        if((int)ordersToDisplay.getCount()%ordersToDisplay.getItemsOnPage()!=0
                || ordersToDisplay.getCount()==0)
            totalPages++;
        ordersToDisplay.setTotalPages(totalPages);
        if(ordersToDisplay.getPage() > ordersToDisplay.getTotalPages())
            ordersToDisplay.setPage(ordersToDisplay.getTotalPages());

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
