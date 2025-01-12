package com.yermaalexx.pizzadelivery;

import com.yermaalexx.pizzadelivery.model.Pizza;
import com.yermaalexx.pizzadelivery.model.PizzaOrder;
import com.yermaalexx.pizzadelivery.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @WithMockUser
    public void testOrderForm() throws Exception {
        mockMvc.perform(get("/orders/current")
                        .sessionAttr("pizzaOrder", new PizzaOrder(null, new Date(), "-", "-", "-", "-", new ArrayList<>())))
                .andExpect(status().isOk())
                .andExpect(view().name("orderForm"))
                .andExpect(model().attributeExists("pizzaOrder"));
    }

    @Test
    @WithMockUser
    public void testProcessOrderSuccessful() throws Exception {
        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.setDeliveryName("user");
        pizzaOrder.setDeliveryPhone("777-77-77");
        pizzaOrder.setDeliveryStreet("Street");
        pizzaOrder.setDeliveryHouse("007");
        orderRepository.deleteAll();

        mockMvc.perform(post("/orders")
                        .flashAttr("pizzaOrder", pizzaOrder))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        PizzaOrder savedOrder = orderRepository.findAll().get(0);
        assertEquals("user", savedOrder.getDeliveryName());
        assertEquals("777-77-77", savedOrder.getDeliveryPhone());
        assertEquals("Street", savedOrder.getDeliveryStreet());
        assertEquals("007", savedOrder.getDeliveryHouse());
    }

    @Test
    @WithMockUser
    public void testDeleteOnePizzaFromList() throws Exception {
        Pizza pizza1 = new Pizza();
        pizza1.setCreatedAt(new Date(1735938237000L));
        Pizza pizza2 = new Pizza();
        pizza2.setCreatedAt(new Date(1735938301000L));
        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.getPizzas().add(pizza1);
        pizzaOrder.getPizzas().add(pizza2);
        String dateOfPizzaToDelete = new Date(1735938237000L).toString();

        mockMvc.perform(post("/orders/deletePizza")
                        .sessionAttr("pizzaOrder", pizzaOrder)
                        .param("listToRemove", dateOfPizzaToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/current"));
        assertEquals(pizzaOrder.getPizzas().size(), 1);
        assertEquals(pizzaOrder.getPizzas().get(0).getCreatedAt().toString(), new Date(1735938301000L).toString());
    }

    @Test
    @WithMockUser
    public void testDeleteAllPizzasFromList() throws Exception {
        Pizza pizza1 = new Pizza();
        pizza1.setCreatedAt(new Date(1735938237000L));
        Pizza pizza2 = new Pizza();
        pizza2.setCreatedAt(new Date(1735938301000L));
        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.getPizzas().add(pizza1);
        pizzaOrder.getPizzas().add(pizza2);
        String dateOfPizzaToDelete1 = new Date(1735938237000L).toString();
        String dateOfPizzaToDelete2 = new Date(1735938301000L).toString();

        mockMvc.perform(post("/orders/deletePizza")
                        .sessionAttr("pizzaOrder", pizzaOrder)
                        .param("listToRemove", dateOfPizzaToDelete1, dateOfPizzaToDelete2))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/design"));
        assertTrue(pizzaOrder.getPizzas().isEmpty());

    }

}
