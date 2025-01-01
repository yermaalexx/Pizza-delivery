package com.yermaalexx.pizzadelivery.controller;

import com.yermaalexx.pizzadelivery.model.ObjectsToDisplay;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.model.UserApp;
import com.yermaalexx.pizzadelivery.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Date;

@Controller
@RequestMapping("/orders")
@SessionAttributes("pizzaOrder")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @ModelAttribute(name = "pizzasToDisplay")
    public ObjectsToDisplay order() {
        return new ObjectsToDisplay();
    }

    @GetMapping("/current")
    public String orderForm(@AuthenticationPrincipal UserApp user,
                            @ModelAttribute PizzaOrder pizzaOrder) {
        if(pizzaOrder.getDeliveryName() == null)
            pizzaOrder.setDeliveryName(user.getUsername());
        if(pizzaOrder.getDeliveryPhone() == null)
            pizzaOrder.setDeliveryPhone(user.getPhone());
        if(pizzaOrder.getDeliveryStreet() == null)
            pizzaOrder.setDeliveryStreet(user.getStreet());
        if(pizzaOrder.getDeliveryHouse() == null)
            pizzaOrder.setDeliveryHouse(user.getHouse());

        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid PizzaOrder pizzaOrder, Errors errors,
                               SessionStatus sessionStatus) {
        if(errors.hasErrors()) {
            return "orderForm";
        }
        pizzaOrder.setPlacedAt(new Date());
        orderRepository.save(pizzaOrder);
        sessionStatus.setComplete();

        return "redirect:/";
    }

    @PostMapping("/deletePizza")
    public String deleteIngredient(@ModelAttribute ObjectsToDisplay pizzasToDisplay, Model model) {
        if(pizzasToDisplay.getListToRemove().isEmpty())
            return "redirect:/orders/current";
        PizzaOrder pizzaOrder = ((PizzaOrder)model.getAttribute("pizzaOrder"));
        if(pizzaOrder != null)
            pizzaOrder.removePizzasByDate(pizzasToDisplay.getListToRemove());
        if(pizzaOrder==null || pizzaOrder.hasNoPizzas())
            return "redirect:/design";
        else
            return "redirect:/orders/current";
    }

}
