package com.bookstore.management.service;

import com.bookstore.management.model.Admin;
import com.bookstore.management.model.Book;
import com.bookstore.management.model.Cart;
import com.bookstore.management.model.Customer;
import com.bookstore.management.model.DigitalBook;
import com.bookstore.management.model.OrderItem;
import com.bookstore.management.model.OrderRecord;
import com.bookstore.management.model.OrderStatus;
import com.bookstore.management.model.PermissionLevel;
import com.bookstore.management.model.PrintedBook;
import com.bookstore.management.repository.AdminRepository;
import com.bookstore.management.repository.BookRepository;
import com.bookstore.management.repository.CartRepository;
import com.bookstore.management.repository.CustomerRepository;
import com.bookstore.management.repository.OrderRepository;
import com.bookstore.management.util.IdGenerator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BootstrapDataService implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public BootstrapDataService(CustomerRepository customerRepository, AdminRepository adminRepository,
                                BookRepository bookRepository, CartRepository cartRepository,
                                OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedCustomers();
        seedAdmins();
        seedBooks();
        seedCarts();
        seedOrders();
    }

    private void seedCustomers() {
        if (customerRepository.count() > 0) {
            return;
        }
        customerRepository.save(new Customer(
                "CUS-A1",
                "Maya Perera",
                "maya@chapterlane.com",
                "+94 71 234 5678",
                "maya123",
                true,
                LocalDateTime.now().minusDays(8),
                "Literary Fiction",
                "Gold",
                "No. 45, Park Road, Colombo"
        ));
        customerRepository.save(new Customer(
                "CUS-B2",
                "Nethmi Silva",
                "nethmi@chapterlane.com",
                "+94 77 987 6543",
                "nethmi123",
                true,
                LocalDateTime.now().minusDays(5),
                "Fantasy",
                "Silver",
                "14 Lake View, Kandy"
        ));
    }

    private void seedAdmins() {
        if (adminRepository.count() > 0) {
            return;
        }
        adminRepository.save(new Admin(
                "ADM-A1",
                "Ariya Fernando",
                "admin@chapterlane.com",
                "+94 70 111 2222",
                "admin123",
                true,
                LocalDateTime.now().minusDays(30),
                PermissionLevel.SUPER_ADMIN,
                "Creative Ops Lead"
        ));
        adminRepository.save(new Admin(
                "ADM-B2",
                "Sajini Rathnayake",
                "ops@chapterlane.com",
                "+94 76 444 5555",
                "ops12345",
                true,
                LocalDateTime.now().minusDays(20),
                PermissionLevel.OPERATIONS_MANAGER,
                "Fulfillment Director"
        ));
    }

    private void seedBooks() {
        if (bookRepository.count() > 0) {
            return;
        }
        List<Book> books = List.of(
                new PrintedBook("BOO-A1", "Midnight Atlas", "H. N. Rowan", "Fantasy", "978-1000000001",
                        "A sweeping fantasy about cartographers who can redraw reality.", 24.99, 18,
                        4.8, true, "#173F5F", "#F6D365", 432, "FAN-14"),
                new PrintedBook("BOO-B2", "The Quiet Signal", "Imesha Costa", "Thriller", "978-1000000002",
                        "A tense newsroom thriller driven by disappearing archives.", 19.50, 9,
                        4.6, true, "#2C5364", "#F8B500", 316, "THR-07"),
                new PrintedBook("BOO-C3", "Designing Delight", "Ria Madugalle", "Design", "978-1000000003",
                        "A visual guide to user-centric product experiences and motion systems.", 28.00, 14,
                        4.9, true, "#114B5F", "#FFD166", 288, "DSN-03"),
                new DigitalBook("BOO-D4", "Algorithmic Bloom", "Kenji Peris", "Technology", "978-1000000004",
                        "A friendly deep dive into modern programming patterns and clean architecture.", 16.99, 35,
                        4.7, false, "#355C7D", "#6C5B7B", "PDF", 6.4),
                new DigitalBook("BOO-E5", "Sea Glass Letters", "Amaya Dias", "Romance", "978-1000000005",
                        "A coastal romance told through hidden notes and unfinished postcards.", 11.99, 42,
                        4.5, false, "#3A506B", "#5BC0BE", "EPUB", 4.1),
                new PrintedBook("BOO-F6", "The Civic Orchard", "Malith Jayasekara", "Non-Fiction", "978-1000000006",
                        "Essays on cities, public life, and why communities flourish.", 22.25, 11,
                        4.4, false, "#0B132B", "#F4D35E", 252, "NFC-09")
        );

        for (Book book : books) {
            bookRepository.save(book);
        }
    }

    private void seedCarts() {
        if (cartRepository.count() > 0) {
            return;
        }
        cartRepository.save(new Cart("CRT-A1", "CUS-A1", LocalDateTime.now().minusHours(4), new ArrayList<>()));
        cartRepository.save(new Cart("CRT-B2", "CUS-B2", LocalDateTime.now().minusHours(2), new ArrayList<>()));
    }

    private void seedOrders() {
        if (orderRepository.count() > 0) {
            return;
        }
        orderRepository.save(new OrderRecord(
                IdGenerator.create("ORDREC"),
                "ORD-20260424073000",
                "CUS-A1",
                "Maya Perera",
                "maya@chapterlane.com",
                "No. 45, Park Road, Colombo",
                "Gift wrap the printed edition.",
                OrderStatus.SHIPPED,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                List.of(
                        new OrderItem("BOO-A1", "Midnight Atlas", "Printed Edition", 1, 24.99),
                        new OrderItem("BOO-D4", "Algorithmic Bloom", "Digital Edition", 1, 16.99)
                )
        ));
    }
}
