package com.bookstore.management.service;

import com.bookstore.management.model.Book;
import com.bookstore.management.model.Cart;
import com.bookstore.management.model.CartItem;
import com.bookstore.management.repository.CartRepository;
import com.bookstore.management.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final BookService bookService;

    public CartService(CartRepository cartRepository, BookService bookService) {
        this.cartRepository = cartRepository;
        this.bookService = bookService;
    }

    public Cart getCartForCustomer(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> createCart(customerId));
    }

    public void addBook(String customerId, String bookId, int quantity) {
        Book book = bookService.requireById(bookId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (quantity > book.getStock()) {
            throw new IllegalArgumentException("Only " + book.getStock() + " copies are available.");
        }

        Cart cart = getCartForCustomer(customerId);
        CartItem item = cart.getItems().stream()
                .filter(existing -> existing.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            cart.getItems().add(new CartItem(
                    book.getId(),
                    book.getTitle(),
                    book.getFormatLabel(),
                    book.getCoverTone(),
                    book.getPrice(),
                    quantity
            ));
        } else {
            int updatedQuantity = item.getQuantity() + quantity;
            if (updatedQuantity > book.getStock()) {
                throw new IllegalArgumentException("Cart quantity exceeds available stock.");
            }
            item.setQuantity(updatedQuantity);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public void updateQuantity(String customerId, String bookId, int quantity) {
        Cart cart = getCartForCustomer(customerId);
        CartItem item = cart.getItems().stream()
                .filter(existing -> existing.getBookId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found."));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            Book book = bookService.requireById(bookId);
            if (quantity > book.getStock()) {
                throw new IllegalArgumentException("Requested quantity exceeds available stock.");
            }
            item.setQuantity(quantity);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public void removeItem(String customerId, String bookId) {
        Cart cart = getCartForCustomer(customerId);
        cart.getItems().removeIf(item -> item.getBookId().equals(bookId));
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public void clearCart(String customerId) {
        Cart cart = getCartForCustomer(customerId);
        cart.setItems(new ArrayList<>());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private Cart createCart(String customerId) {
        Cart cart = new Cart(IdGenerator.create("CRT"), customerId, LocalDateTime.now(), new ArrayList<>());
        cartRepository.save(cart);
        return cart;
    }
}
