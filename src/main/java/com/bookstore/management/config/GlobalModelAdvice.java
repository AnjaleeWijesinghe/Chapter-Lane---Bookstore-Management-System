package com.bookstore.management.config;

import com.bookstore.management.model.Admin;
import com.bookstore.management.model.Customer;
import com.bookstore.management.service.CartService;
import com.bookstore.management.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final SessionService sessionService;
    private final CartService cartService;

    public GlobalModelAdvice(SessionService sessionService, CartService cartService) {
        this.sessionService = sessionService;
        this.cartService = cartService;
    }

    @ModelAttribute("currentCustomer")
    public Customer currentCustomer(HttpSession session) {
        return sessionService.getCurrentCustomer(session).orElse(null);
    }

    @ModelAttribute("currentAdmin")
    public Admin currentAdmin(HttpSession session) {
        return sessionService.getCurrentAdmin(session).orElse(null);
    }

    @ModelAttribute("isCustomerAuthenticated")
    public boolean isCustomerAuthenticated(HttpSession session) {
        return sessionService.isCustomerAuthenticated(session);
    }

    @ModelAttribute("isAdminAuthenticated")
    public boolean isAdminAuthenticated(HttpSession session) {
        return sessionService.isAdminAuthenticated(session);
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount(HttpSession session) {
        return sessionService.getCurrentCustomer(session)
                .map(customer -> cartService.getCartForCustomer(customer.getId()).getTotalItems())
                .orElse(0);
    }

    @ModelAttribute("currentYear")
    public int currentYear() {
        return java.time.Year.now().getValue();
    }
}
