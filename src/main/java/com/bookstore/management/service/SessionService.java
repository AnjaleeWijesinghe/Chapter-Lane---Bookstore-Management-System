package com.bookstore.management.service;

import com.bookstore.management.model.Admin;
import com.bookstore.management.model.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    private static final String CUSTOMER_ID = "customerId";
    private static final String ADMIN_ID = "adminId";

    private final CustomerService customerService;
    private final AdminService adminService;

    public SessionService(CustomerService customerService, AdminService adminService) {
        this.customerService = customerService;
        this.adminService = adminService;
    }

    public void loginCustomer(HttpSession session, Customer customer) {
        session.removeAttribute(ADMIN_ID);
        session.setAttribute(CUSTOMER_ID, customer.getId());
    }

    public void loginAdmin(HttpSession session, Admin admin) {
        session.removeAttribute(CUSTOMER_ID);
        session.setAttribute(ADMIN_ID, admin.getId());
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public Optional<Customer> getCurrentCustomer(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object id = session.getAttribute(CUSTOMER_ID);
        return id instanceof String customerId ? customerService.findById(customerId) : Optional.empty();
    }

    public Optional<Admin> getCurrentAdmin(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object id = session.getAttribute(ADMIN_ID);
        return id instanceof String adminId ? adminService.findById(adminId) : Optional.empty();
    }

    public boolean isCustomerAuthenticated(HttpSession session) {
        return getCurrentCustomer(session).isPresent();
    }

    public boolean isAdminAuthenticated(HttpSession session) {
        return getCurrentAdmin(session).isPresent();
    }
}
