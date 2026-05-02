package com.bookstore.management.service;

import com.bookstore.management.dto.AdminForm;
import com.bookstore.management.model.Admin;
import com.bookstore.management.model.PermissionLevel;
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
public class AdminService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    public AdminService(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    public List<Admin> listAdmins() {
        return adminRepository.findAllAdmins().stream()
                .sorted(Comparator.comparing(Admin::getCreatedAt).reversed())
                .toList();
    }

    public Optional<Admin> authenticate(String email, String password) {
        return adminRepository.findByEmail(normalize(email))
                .filter(Admin::isActive)
                .filter(admin -> admin.getPassword().equals(password));
    }

    public Optional<Admin> findById(String adminId) {
        return adminRepository.findById(adminId);
    }

    public Admin requireById(String adminId) {
        return findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found."));
    }

    public Admin save(AdminForm form) {
        validateUniqueEmail(form.getEmail(), form.getId());
        Admin admin = form.getId() == null || form.getId().isBlank()
                ? new Admin(
                IdGenerator.create("ADM"),
                form.getFullName().trim(),
                normalize(form.getEmail()),
                form.getPhone().trim(),
                requirePassword(form.getPassword()),
                true,
                LocalDateTime.now(),
                form.getPermissionLevel(),
                form.getBadgeLabel().trim())
                : updateExisting(form);
        adminRepository.save(admin);
        return admin;
    }

    public void delete(String adminId) {
        adminRepository.deleteById(adminId);
    }

    public AdminForm toForm(Admin admin) {
        AdminForm form = new AdminForm();
        form.setId(admin.getId());
        form.setFullName(admin.getFullName());
        form.setEmail(admin.getEmail());
        form.setPhone(admin.getPhone());
        form.setPassword(admin.getPassword());
        form.setPermissionLevel(admin.getPermissionLevel());
        form.setBadgeLabel(admin.getBadgeLabel());
        return form;
    }

    public PermissionLevel[] permissionLevels() {
        return PermissionLevel.values();
    }

    public long count() {
        return adminRepository.count();
    }

    private Admin updateExisting(AdminForm form) {
        Admin admin = requireById(form.getId());
        admin.setFullName(form.getFullName().trim());
        admin.setEmail(normalize(form.getEmail()));
        admin.setPhone(form.getPhone().trim());
        admin.setPermissionLevel(form.getPermissionLevel());
        admin.setBadgeLabel(form.getBadgeLabel().trim());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            admin.setPassword(form.getPassword().trim());
        }
        return admin;
    }

    private void validateUniqueEmail(String email, String existingAdminId) {
        String normalizedEmail = normalize(email);
        boolean usedByAdmin = adminRepository.findByEmail(normalizedEmail)
                .filter(admin -> !admin.getId().equals(existingAdminId))
                .isPresent();
        boolean usedByCustomer = customerRepository.findByEmail(normalizedEmail).isPresent();
        if (usedByAdmin || usedByCustomer) {
            throw new IllegalArgumentException("Email is already used by another account.");
        }
    }

    private String requirePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required for a new admin.");
        }
        return password.trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
