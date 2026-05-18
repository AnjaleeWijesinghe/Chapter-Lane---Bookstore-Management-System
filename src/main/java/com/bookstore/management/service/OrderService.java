// ORDER SERVICE - U.L.G.Maduwantha - IT19010618
// Handles all business logic for order management
package com.bookstore.management.service;

import com.bookstore.management.dto.CheckoutForm;
import com.bookstore.management.model.Book;
import com.bookstore.management.model.Cart;
import com.bookstore.management.model.CartItem;
import com.bookstore.management.model.Customer;
import com.bookstore.management.model.OrderItem;
import com.bookstore.management.model.OrderRecord;
import com.bookstore.management.model.OrderStatus;
import com.bookstore.management.repository.OrderRepository;
import com.bookstore.management.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class OrderService {

    // DEPENDENCY INJECTION
    // Inject required services and repository

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final BookService bookService;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository, CartService cartService, BookService bookService,
                        CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.bookService = bookService;
        this.customerService = customerService;
    }

    // PLACE ORDER
    // Validates cart and stock, creates order record,
    // decreases book stock, and clears the cart

    public OrderRecord placeOrder(String customerId, CheckoutForm form) {
        Customer customer = customerService.requireById(customerId);
        Cart cart = cartService.getCartForCustomer(customerId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }

        for (CartItem item : cart.getItems()) {
            Book book = bookService.requireById(item.getBookId());
            if (book.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + book.getTitle() + ".");
            }
        }

        List<OrderItem> items = cart.getItems().stream()
                .map(item -> new OrderItem(
                        item.getBookId(),
                        item.getTitle(),
                        item.getFormatLabel(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        LocalDateTime now = LocalDateTime.now();
        OrderRecord order = new OrderRecord(
                IdGenerator.create("ORDREC"),
                IdGenerator.createOrderNumber(),
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                form.getShippingAddress().trim(),
                form.getNote() == null ? "" : form.getNote().trim(),
                OrderStatus.PENDING,
                now,
                now,
                items
        );

        orderRepository.save(order);
        for (CartItem item : cart.getItems()) {
            bookService.decreaseStock(item.getBookId(), item.getQuantity());
        }
        cartService.clearCart(customerId);
        return order;
    }

    //  LIST CUSTOMER ORDERS
    // Returns all orders for a specific customer
    // sorted by newest first

    public List<OrderRecord> listCustomerOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .sorted(Comparator.comparing(OrderRecord::getCreatedAt).reversed())
                .toList();
    }

    // LIST ALL ORDERS (ADMIN)
    // Returns all orders with optional search filter
    // and status filter, sorted by newest first

    public List<OrderRecord> listAll(String query, String status) {
        String normalizedQuery = normalize(query);
        String normalizedStatus = normalize(status);
        return orderRepository.findAllOrders().stream()
                .filter(order -> normalizedQuery.isBlank()
                        || contains(order.getOrderNumber(), normalizedQuery)
                        || contains(order.getCustomerName(), normalizedQuery)
                        || contains(order.getCustomerEmail(), normalizedQuery))
                .filter(order -> normalizedStatus.isBlank() || order.getStatus().name().equalsIgnoreCase(normalizedStatus))
                .sorted(Comparator.comparing(OrderRecord::getCreatedAt).reversed())
                .toList();
    }

    // UPDATE ORDER STATUS (ADMIN)
    // Updates order status, restores stock if cancelled
    // Cancelled orders cannot be re-opened

    public OrderRecord updateStatus(String orderId, OrderStatus status) {
        OrderRecord order = requireById(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED && status != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled orders cannot be re-opened.");
        }
        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            restoreStock(order);
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        return order;
    }

    // CANCEL ORDER BY CUSTOMER
    // Allows customer to cancel their own order
    // Restores stock on cancellation
    // Cannot cancel shipped or delivered orders

    public OrderRecord cancelByCustomer(String customerId, String orderId) {
        OrderRecord order = requireById(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("You cannot cancel another customer's order.");
        }
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Shipped or delivered orders cannot be cancelled.");
        }
        if (order.getStatus() != OrderStatus.CANCELLED) {
            restoreStock(order);
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
        return order;
    }

    // DELETE ORDER (ADMIN)
    // Only cancelled or delivered orders can be deleted

    public void delete(String orderId) {
        OrderRecord order = requireById(orderId);
        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Only cancelled or delivered orders can be deleted.");
        }
        orderRepository.deleteById(orderId);
    }

    // HELPER METHODS
    // requireById   - finds order or throws error
    // count         - total number of orders
    // activeOrderCount - orders not cancelled/delivered
    // revenue       - total revenue excluding cancelled
    // statusOptions - returns all status enum values

    public OrderRecord requireById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
    }

    public long count() {
        return orderRepository.count();
    }

    public long activeOrderCount() {
        return orderRepository.findAllOrders().stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.DELIVERED)
                .count();
    }

    public double revenue() {
        return orderRepository.findAllOrders().stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(OrderRecord::getGrandTotal)
                .sum();
    }

    public OrderStatus[] statusOptions() {
        return OrderStatus.values();
    }

    private void restoreStock(OrderRecord order) {
        for (OrderItem item : order.getItems()) {
            bookService.increaseStock(item.getBookId(), item.getQuantity());
        }
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
