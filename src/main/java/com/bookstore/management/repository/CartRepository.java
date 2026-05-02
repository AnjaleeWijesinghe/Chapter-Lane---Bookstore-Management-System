package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CartRepository extends JsonLineFileRepository<Cart> {

    public CartRepository(ObjectMapper objectMapper, StorageProperties storageProperties) {
        super(objectMapper, storageProperties, storageProperties.getCartsFile(), Cart.class);
    }

    public Optional<Cart> findByCustomerId(String customerId) {
        return findOne(cart -> cart.getCustomerId().equals(customerId));
    }

    public void save(Cart cart) {
        store(cart, Cart::getId);
    }

    public void deleteByCustomerId(String customerId) {
        deleteWhere(cart -> cart.getCustomerId().equals(customerId));
    }

    public long count() {
        return readAll().size();
    }
}
