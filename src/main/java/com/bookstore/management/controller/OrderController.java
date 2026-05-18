// ORDER CONTROLLER - U.L.G.Maduwantha - IT19010618
// Handles HTTP requests for order management
// Customer order view, checkout, and cancel
package com.bookstore.management.controller;

import com.bookstore.management.dto.CheckoutForm;
import com.bookstore.management.model.Customer;
import com.bookstore.management.service.OrderService;
import com.bookstore.management.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    //DEPENDENCY INJECTION
    // Inject OrderService and SessionService

    private final OrderService orderService;
    private final SessionService sessionService;

    public OrderController(OrderService orderService, SessionService sessionService) {
        this.orderService = orderService;
        this.sessionService = sessionService;
    }

    // VIEW ORDERS - GET /orders
    // Loads all orders for the logged-in customer
    // and displays them on the orders page

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Customer customer = currentCustomer(session);
        model.addAttribute("orders", orderService.listCustomerOrders(customer.getId()));
        return "orders";
    }

    @PostMapping("/orders/checkout")
    public String checkout(@Valid @ModelAttribute("checkoutForm") CheckoutForm form,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Customer customer = currentCustomer(session);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please review the shipping details.");
            return "redirect:/cart";
        }

        try {
            orderService.placeOrder(customer.getId(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully.");
            return "redirect:/orders";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancel(@PathVariable String orderId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelByCustomer(currentCustomer(session).getId(), orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/orders";
    }

    private Customer currentCustomer(HttpSession session) {
        return sessionService.getCurrentCustomer(session)
                .orElseThrow(() -> new IllegalArgumentException("Customer session not found."));
    }
}
