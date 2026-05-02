package com.bookstore.management.controller;

import com.bookstore.management.dto.CheckoutForm;
import com.bookstore.management.model.Customer;
import com.bookstore.management.service.CartService;
import com.bookstore.management.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;
    private final SessionService sessionService;

    public CartController(CartService cartService, SessionService sessionService) {
        this.cartService = cartService;
        this.sessionService = sessionService;
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        Customer customer = currentCustomer(session);
        model.addAttribute("cart", cartService.getCartForCustomer(customer.getId()));
        if (!model.containsAttribute("checkoutForm")) {
            CheckoutForm checkoutForm = new CheckoutForm();
            checkoutForm.setShippingAddress(customer.getShippingAddress());
            model.addAttribute("checkoutForm", checkoutForm);
        }
        return "cart";
    }

    @PostMapping("/cart/add/{bookId}")
    public String addToCart(@PathVariable String bookId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.addBook(currentCustomer(session).getId(), bookId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Book added to your cart.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/catalog";
    }

    @PostMapping("/cart/update/{bookId}")
    public String updateQuantity(@PathVariable String bookId,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(currentCustomer(session).getId(), bookId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{bookId}")
    public String removeItem(@PathVariable String bookId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        cartService.removeItem(currentCustomer(session).getId(), bookId);
        redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clearCart(currentCustomer(session).getId());
        redirectAttributes.addFlashAttribute("successMessage", "Cart cleared.");
        return "redirect:/cart";
    }

    private Customer currentCustomer(HttpSession session) {
        return sessionService.getCurrentCustomer(session)
                .orElseThrow(() -> new IllegalArgumentException("Customer session not found."));
    }
}
