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

}

