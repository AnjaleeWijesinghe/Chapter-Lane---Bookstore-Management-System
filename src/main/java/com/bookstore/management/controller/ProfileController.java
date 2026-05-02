package com.bookstore.management.controller;

import com.bookstore.management.dto.CustomerProfileForm;
import com.bookstore.management.model.Customer;
import com.bookstore.management.service.CustomerService;
import com.bookstore.management.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final CustomerService customerService;
    private final SessionService sessionService;

    public ProfileController(CustomerService customerService, SessionService sessionService) {
        this.customerService = customerService;
        this.sessionService = sessionService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Customer customer = sessionService.getCurrentCustomer(session)
                .orElseThrow(() -> new IllegalArgumentException("Customer session not found."));
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", customerService.toProfileForm(customer));
        }
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") CustomerProfileForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Customer customer = sessionService.getCurrentCustomer(session)
                .orElseThrow(() -> new IllegalArgumentException("Customer session not found."));

        if (bindingResult.hasErrors()) {
            return "profile";
        }

        try {
            customerService.updateProfile(customer.getId(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(HttpSession session, RedirectAttributes redirectAttributes) {
        Customer customer = sessionService.getCurrentCustomer(session)
                .orElseThrow(() -> new IllegalArgumentException("Customer session not found."));
        customerService.deleteCustomer(customer.getId());
        sessionService.logout(session);
        redirectAttributes.addFlashAttribute("successMessage", "Account removed successfully.");
        return "redirect:/";
    }
}
