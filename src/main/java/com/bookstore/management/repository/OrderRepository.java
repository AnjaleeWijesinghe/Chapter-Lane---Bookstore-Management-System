package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.OrderRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepository extends JsonLineFileRepository<OrderRecord> {

    public OrderRepository(ObjectMapper objectMapper, StorageProperties storageProperties) {
        super(objectMapper, storageProperties, storageProperties.getOrdersFile(), OrderRecord.class);
    }

    public List<OrderRecord> findAllOrders() {
        return readAll();
    }

    public List<OrderRecord> findByCustomerId(String customerId) {
        return readAll().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public Optional<OrderRecord> findById(String orderId) {
        return findOne(order -> order.getId().equals(orderId));
    }

    public void save(OrderRecord orderRecord) {
        store(orderRecord, OrderRecord::getId);
    }

    public void deleteById(String orderId) {
        deleteWhere(order -> order.getId().equals(orderId));
    }

    public long count() {
        return readAll().size();
    }
}
