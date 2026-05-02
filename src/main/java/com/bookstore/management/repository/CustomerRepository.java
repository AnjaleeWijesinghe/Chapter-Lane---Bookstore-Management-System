package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository extends JsonLineFileRepository<Customer> {

    public CustomerRepository(ObjectMapper objectMapper, StorageProperties storageProperties) {
        super(objectMapper, storageProperties, storageProperties.getCustomersFile(), Customer.class);
    }

    public List<Customer> findAllCustomers() {
        return readAll();
    }

    public Optional<Customer> findById(String customerId) {
        return findOne(customer -> customer.getId().equals(customerId));
    }

    public Optional<Customer> findByEmail(String email) {
        return findOne(customer -> customer.getEmail().equalsIgnoreCase(email));
    }

    public void save(Customer customer) {
        store(customer, Customer::getId);
    }

    public void deleteById(String customerId) {
        deleteWhere(customer -> customer.getId().equals(customerId));
    }

    public long count() {
        return readAll().size();
    }
}
