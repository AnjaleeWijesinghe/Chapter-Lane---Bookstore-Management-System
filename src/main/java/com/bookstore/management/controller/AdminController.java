package com.bookstore.management.controller;

import com.bookstore.management.dto.AdminForm;
import com.bookstore.management.dto.BookForm;
import com.bookstore.management.model.Admin;
import com.bookstore.management.model.OrderStatus;
import com.bookstore.management.service.AdminService;
import com.bookstore.management.service.AuditService;
import com.bookstore.management.service.BookService;
import com.bookstore.management.service.CustomerService;
import com.bookstore.management.service.OrderService;
import com.bookstore.management.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final AdminService adminService;
    private final AuditService auditService;
    private final BookService bookService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final SessionService sessionService;

    public AdminController(AdminService adminService, AuditService auditService, BookService bookService,
                           CustomerService customerService, OrderService orderService, SessionService sessionService) {
        this.adminService = adminService;
        this.auditService = auditService;
        this.bookService = bookService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.sessionService = sessionService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("bookCount", bookService.count());
        model.addAttribute("customerCount", customerService.count());
        model.addAttribute("orderCount", orderService.count());
        model.addAttribute("totalStock", bookService.totalStock());
        model.addAttribute("revenue", orderService.revenue());
        model.addAttribute("recentLogs", auditService.recentEntries(6));
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String users(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("users", customerService.listCustomers(query));
        model.addAttribute("query", query == null ? "" : query);
        return "admin/users";
    }

    @PostMapping("/admin/users/{customerId}/delete")
    public String deleteUser(@PathVariable String customerId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(customerId);
            auditService.log(currentAdmin(session), "DELETE", "CUSTOMER", customerId, "Deleted customer account.");
            redirectAttributes.addFlashAttribute("successMessage", "Customer removed.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/books")
    public String books(@RequestParam(required = false) String query,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String edit,
                        Model model) {
        BookForm bookForm = (BookForm) model.getAttribute("bookForm");
        if (bookForm == null) {
            bookForm = edit == null || edit.isBlank()
                    ? bookService.emptyForm()
                    : bookService.toForm(bookService.requireById(edit));
        }
        populateBookModel(model, query, category, bookForm, edit);
        return "admin/books";
    }

    @PostMapping("/admin/books")
    public String saveBook(@Valid @ModelAttribute("bookForm") BookForm form,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBookModel(model, "", "", form, form.getId());
            return "admin/books";
        }

        try {
            boolean updating = form.getId() != null && !form.getId().isBlank();
            var savedBook = bookService.save(form);
            auditService.log(currentAdmin(session), updating ? "UPDATE" : "CREATE",
                    "BOOK", savedBook.getId(), (updating ? "Updated" : "Created") + " book " + savedBook.getTitle());
            redirectAttributes.addFlashAttribute("successMessage",
                    updating ? "Book updated successfully." : "Book created successfully.");
            return "redirect:/admin/books";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            populateBookModel(model, "", "", form, form.getId());
            return "admin/books";
        }
    }

    @PostMapping("/admin/books/{bookId}/delete")
    public String deleteBook(@PathVariable String bookId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            String title = bookService.requireById(bookId).getTitle();
            bookService.delete(bookId);
            auditService.log(currentAdmin(session), "DELETE", "BOOK", bookId, "Deleted book " + title);
            redirectAttributes.addFlashAttribute("successMessage", "Book removed successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/admin/orders")
    public String orders(@RequestParam(required = false) String query,
                         @RequestParam(required = false) String status,
                         Model model) {
        model.addAttribute("orders", orderService.listAll(query, status));
        model.addAttribute("statusOptions", orderService.statusOptions());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedStatus", status == null ? "" : status);
        return "admin/orders";
    }

    @PostMapping("/admin/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable String orderId,
                                    @RequestParam OrderStatus status,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        try {
            var order = orderService.updateStatus(orderId, status);
            auditService.log(currentAdmin(session), "UPDATE", "ORDER", orderId,
                    "Changed order " + order.getOrderNumber() + " status to " + status.name());
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/admin/orders/{orderId}/delete")
    public String deleteOrder(@PathVariable String orderId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            String orderNumber = orderService.requireById(orderId).getOrderNumber();
            orderService.delete(orderId);
            auditService.log(currentAdmin(session), "DELETE", "ORDER", orderId, "Deleted order " + orderNumber);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/admins")
    public String admins(@RequestParam(required = false) String edit, Model model) {
        AdminForm adminForm = (AdminForm) model.getAttribute("adminForm");
        if (adminForm == null) {
            adminForm = edit == null || edit.isBlank()
                    ? new AdminForm()
                    : adminService.toForm(adminService.requireById(edit));
        }
        populateAdminModel(model, adminForm, edit);
        return "admin/admins";
    }

    @PostMapping("/admin/admins")
    public String saveAdmin(@Valid @ModelAttribute("adminForm") AdminForm form,
                            BindingResult bindingResult,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateAdminModel(model, form, form.getId());
            return "admin/admins";
        }

        try {
            boolean updating = form.getId() != null && !form.getId().isBlank();
            Admin savedAdmin = adminService.save(form);
            auditService.log(currentAdmin(session), updating ? "UPDATE" : "CREATE",
                    "ADMIN", savedAdmin.getId(), (updating ? "Updated" : "Created") + " admin " + savedAdmin.getFullName());
            redirectAttributes.addFlashAttribute("successMessage",
                    updating ? "Admin updated successfully." : "Admin added successfully.");
            return "redirect:/admin/admins";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            populateAdminModel(model, form, form.getId());
            return "admin/admins";
        }
    }

    @PostMapping("/admin/admins/{adminId}/delete")
    public String deleteAdmin(@PathVariable String adminId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            Admin actingAdmin = currentAdmin(session);
            if (actingAdmin.getId().equals(adminId)) {
                throw new IllegalArgumentException("You cannot delete your own admin account.");
            }
            String name = adminService.requireById(adminId).getFullName();
            adminService.delete(adminId);
            auditService.log(actingAdmin, "DELETE", "ADMIN", adminId, "Deleted admin " + name);
            redirectAttributes.addFlashAttribute("successMessage", "Admin removed.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/admins";
    }

    private void populateBookModel(Model model, String query, String category, BookForm bookForm, String editId) {
        model.addAttribute("books", bookService.listCatalog(query, category));
        model.addAttribute("categories", bookService.categories());
        model.addAttribute("bookForm", bookForm);
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedCategory", category == null ? "" : category);
        model.addAttribute("editingBookId", editId == null ? "" : editId);
    }

    private void populateAdminModel(Model model, AdminForm adminForm, String editId) {
        model.addAttribute("admins", adminService.listAdmins());
        model.addAttribute("adminForm", adminForm);
        model.addAttribute("permissionLevels", adminService.permissionLevels());
        model.addAttribute("editingAdminId", editId == null ? "" : editId);
    }

    private Admin currentAdmin(HttpSession session) {
        return sessionService.getCurrentAdmin(session)
                .orElseThrow(() -> new IllegalArgumentException("Admin session not found."));
    }
}
