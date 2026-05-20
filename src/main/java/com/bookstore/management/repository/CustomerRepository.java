package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for handling Customer data persistence.
 *
 * This class extends JsonLineFileRepository which handles
 * reading/writing objects to a JSON line-based file.
 */
@Repository
public class CustomerRepository extends JsonLineFileRepository<Customer> {

    /**
     * Constructor injects dependencies and passes configuration
     * to the parent repository class.
     */
    public CustomerRepository(ObjectMapper objectMapper,
                              StorageProperties storageProperties) {

        // Calls parent constructor with:
        // 1. JSON mapper
        // 2. Storage configuration
        // 3. File location for customers data
        // 4. Class type (Customer)
        super(
                objectMapper,
                storageProperties,
                storageProperties.getCustomersFile(),
                Customer.class
        );
    }

    /**
     * Retrieve all customers from storage.
     * @return list of all customers
     */
    public List<Customer> findAllCustomers() {
        return readAll(); // inherited method from parent class
    }

    /**
     * Find a customer by their unique ID.
     * @param customerId ID of customer
     * @return Optional containing Customer if found, otherwise empty
     */
    public Optional<Customer> findById(String customerId) {
        return findOne(customer -> customer.getId().equals(customerId));
    }

    /**
     * Find a customer by email address (case-insensitive).
     * @param email email to search for
     * @return Optional containing Customer if found
     */
    public Optional<Customer> findByEmail(String email) {
        return findOne(customer ->
                customer.getEmail().equalsIgnoreCase(email)
        );
    }

    /**
     * Save or update a customer.
     * If a customer with the same ID exists, it will be replaced.
     *
     * @param customer Customer object to store
     */
    public void save(Customer customer) {
        store(customer, Customer::getId); // uses ID as unique key
    }

    /**
     * Delete a customer using their ID.
     * @param customerId ID of customer to delete
     */
    public void deleteById(String customerId) {
        deleteWhere(customer ->
                customer.getId().equals(customerId)
        );
    }

    /**
     * Count total number of customers in storage.
     * @return number of customers
     */
    public long count() {
        return readAll().size();
    }
}