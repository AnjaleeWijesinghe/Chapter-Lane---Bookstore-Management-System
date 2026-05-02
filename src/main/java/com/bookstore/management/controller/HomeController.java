package com.bookstore.management.controller;

import com.bookstore.management.service.BookService;
import com.bookstore.management.service.CustomerService;
import com.bookstore.management.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final BookService bookService;
    private final CustomerService customerService;
    private final OrderService orderService;

    public HomeController(BookService bookService, CustomerService customerService, OrderService orderService) {
        this.bookService = bookService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredBooks", bookService.featuredBooks(4));
        model.addAttribute("totalBooks", bookService.count());
        model.addAttribute("totalCustomers", customerService.count());
        model.addAttribute("activeOrders", orderService.activeOrderCount());
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) String query,
                          @RequestParam(required = false) String category,
                          Model model) {
        model.addAttribute("books", bookService.listCatalog(query, category));
        model.addAttribute("categories", bookService.categories());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedCategory", category == null ? "" : category);
        return "catalog";
    }
}
