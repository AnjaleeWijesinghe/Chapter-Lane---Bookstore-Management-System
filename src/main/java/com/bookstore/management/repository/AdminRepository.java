package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepository extends JsonLineFileRepository<Admin> {

    public AdminRepository(ObjectMapper objectMapper, StorageProperties storageProperties) {
        super(objectMapper, storageProperties, storageProperties.getAdminsFile(), Admin.class);
    }

    public List<Admin> findAllAdmins() {
        return readAll();
    }

    public Optional<Admin> findById(String adminId) {
        return findOne(admin -> admin.getId().equals(adminId));
    }

    public Optional<Admin> findByEmail(String email) {
        return findOne(admin -> admin.getEmail().equalsIgnoreCase(email));
    }

    public void save(Admin admin) {
        store(admin, Admin::getId);
    }

    public void deleteById(String adminId) {
        deleteWhere(admin -> admin.getId().equals(adminId));
    }

    public long count() {
        return readAll().size();
    }
}
