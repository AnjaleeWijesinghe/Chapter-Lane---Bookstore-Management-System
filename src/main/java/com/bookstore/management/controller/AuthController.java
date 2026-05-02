package com.bookstore.management.controller;

import com.bookstore.management.dto.CustomerRegistrationForm;
import com.bookstore.management.dto.LoginForm;
import com.bookstore.management.model.Admin;
import com.bookstore.management.model.Customer;
import com.bookstore.management.service.AdminService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final CustomerService customerService;
    private final AdminService adminService;
    private final SessionService sessionService;

    public AuthController(CustomerService customerService, AdminService adminService, SessionService sessionService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.sessionService = sessionService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String required,
                            HttpSession session,
                            Model model) {
        if (sessionService.isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        if (sessionService.isCustomerAuthenticated(session)) {
            return "redirect:/catalog";
        }
        if (!model.containsAttribute("loginForm")) {
            LoginForm form = new LoginForm();
            form.setRole("customer");
            model.addAttribute("loginForm", form);
        }
        model.addAttribute("requiredRole", required == null ? "" : required);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("requiredRole", "");
            return "auth/login";
        }

        try {
            if ("admin".equalsIgnoreCase(form.getRole())) {
                Admin admin = adminService.authenticate(form.getEmail(), form.getPassword())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid admin credentials."));
                sessionService.loginAdmin(session, admin);
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + admin.getFullName() + ".");
                return "redirect:/admin";
            }

            Customer customer = customerService.authenticate(form.getEmail(), form.getPassword())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid customer credentials."));
            sessionService.loginCustomer(session, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + customer.getFullName() + ".");
            return "redirect:/catalog";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("requiredRole", "");
            model.addAttribute("errorMessage", exception.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new CustomerRegistrationForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") CustomerRegistrationForm form,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            Customer customer = customerService.register(form);
            sessionService.loginCustomer(session, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Your account is ready. Start building your shelf.");
            return "redirect:/catalog";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
            redirectAttributes.addFlashAttribute("registrationForm", form);
            return "redirect:/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        sessionService.logout(session);
        redirectAttributes.addFlashAttribute("successMessage", "You have been signed out.");
        return "redirect:/";
    }
}
