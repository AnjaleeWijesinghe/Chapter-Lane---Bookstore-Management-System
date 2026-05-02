package com.bookstore.management.service;

import com.bookstore.management.dto.CustomerProfileForm;
import com.bookstore.management.dto.CustomerRegistrationForm;
import com.bookstore.management.model.Customer;
import com.bookstore.management.repository.AdminRepository;
import com.bookstore.management.repository.CustomerRepository;
import com.bookstore.management.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    public CustomerService(CustomerRepository customerRepository, AdminRepository adminRepository) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }

    public List<Customer> listCustomers(String query) {
        String normalized = normalize(query);
        return customerRepository.findAllCustomers().stream()
                .filter(customer -> normalized.isBlank()
                        || contains(customer.getFullName(), normalized)
                        || contains(customer.getEmail(), normalized)
                        || contains(customer.getFavoriteGenre(), normalized)
                        || contains(customer.getLoyaltyTier(), normalized))
                .sorted(Comparator.comparing(Customer::getCreatedAt).reversed())
                .toList();
    }

    public Customer register(CustomerRegistrationForm form) {
        validateUniqueEmail(form.getEmail(), null);
        Customer customer = new Customer(
                IdGenerator.create("CUS"),
                form.getFullName().trim(),
                normalizeEmail(form.getEmail()),
                form.getPhone().trim(),
                form.getPassword().trim(),
                true,
                LocalDateTime.now(),
                form.getFavoriteGenre().trim(),
                form.getLoyaltyTier().trim(),
                form.getShippingAddress().trim()
        );
        customerRepository.save(customer);
        return customer;
    }

    public Optional<Customer> authenticate(String email, String password) {
        return customerRepository.findByEmail(normalizeEmail(email))
                .filter(Customer::isActive)
                .filter(customer -> customer.getPassword().equals(password));
    }

    public Optional<Customer> findById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer requireById(String customerId) {
        return findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
    }

    public Customer updateProfile(String customerId, CustomerProfileForm form) {
        Customer customer = requireById(customerId);
        validateUniqueEmail(form.getEmail(), customerId);
        customer.setFullName(form.getFullName().trim());
        customer.setEmail(normalizeEmail(form.getEmail()));
        customer.setPhone(form.getPhone().trim());
        customer.setFavoriteGenre(form.getFavoriteGenre().trim());
        customer.setLoyaltyTier(form.getLoyaltyTier().trim());
        customer.setShippingAddress(form.getShippingAddress().trim());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            customer.setPassword(form.getPassword().trim());
        }
        customerRepository.save(customer);
        return customer;
    }

    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId);
    }

    public CustomerProfileForm toProfileForm(Customer customer) {
        CustomerProfileForm form = new CustomerProfileForm();
        form.setFullName(customer.getFullName());
        form.setEmail(customer.getEmail());
        form.setPhone(customer.getPhone());
        form.setPassword(customer.getPassword());
        form.setFavoriteGenre(customer.getFavoriteGenre());
        form.setLoyaltyTier(customer.getLoyaltyTier());
        form.setShippingAddress(customer.getShippingAddress());
        return form;
    }

    public long count() {
        return customerRepository.count();
    }

    private void validateUniqueEmail(String email, String existingCustomerId) {
        String normalizedEmail = normalizeEmail(email);
        boolean usedByCustomer = customerRepository.findByEmail(normalizedEmail)
                .filter(customer -> !customer.getId().equals(existingCustomerId))
                .isPresent();
        boolean usedByAdmin = adminRepository.findByEmail(normalizedEmail).isPresent();
        if (usedByCustomer || usedByAdmin) {
            throw new IllegalArgumentException("Email is already used by another account.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalizeEmail(String email) {
        return normalize(email);
    }
}
