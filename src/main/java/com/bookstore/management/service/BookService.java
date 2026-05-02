package com.bookstore.management.service;

import com.bookstore.management.dto.BookForm;
import com.bookstore.management.model.Book;
import com.bookstore.management.model.DigitalBook;
import com.bookstore.management.model.PrintedBook;
import com.bookstore.management.repository.BookRepository;
import com.bookstore.management.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> listCatalog(String query, String category) {
        String normalizedQuery = normalize(query);
        String normalizedCategory = normalize(category);
        return bookRepository.findAllBooks().stream()
                .filter(book -> normalizedQuery.isBlank()
                        || contains(book.getTitle(), normalizedQuery)
                        || contains(book.getAuthor(), normalizedQuery)
                        || contains(book.getCategory(), normalizedQuery)
                        || contains(book.getFormatLabel(), normalizedQuery))
                .filter(book -> normalizedCategory.isBlank() || contains(book.getCategory(), normalizedCategory))
                .sorted(Comparator.comparing(Book::isFeatured).reversed()
                        .thenComparing(Book::getRating).reversed()
                        .thenComparing(Book::getTitle))
                .toList();
    }

    public List<Book> featuredBooks(int limit) {
        return bookRepository.findAllBooks().stream()
                .filter(Book::isFeatured)
                .sorted(Comparator.comparing(Book::getRating).reversed())
                .limit(limit)
                .toList();
    }

    public Book requireById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));
    }

    public Book save(BookForm form) {
        validateTypeSpecificFields(form);
        validateUniqueIsbn(form.getIsbn(), form.getId());

        Book book = switch (normalize(form.getBookType())) {
            case "printed" -> buildPrintedBook(form);
            case "digital" -> buildDigitalBook(form);
            default -> throw new IllegalArgumentException("Invalid book type selected.");
        };

        bookRepository.save(book);
        return book;
    }

    public void delete(String bookId) {
        bookRepository.deleteById(bookId);
    }

    public void decreaseStock(String bookId, int quantity) {
        Book book = requireById(bookId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (book.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock for " + book.getTitle() + ".");
        }
        book.setStock(book.getStock() - quantity);
        bookRepository.save(book);
    }

    public void increaseStock(String bookId, int quantity) {
        Book book = requireById(bookId);
        book.setStock(book.getStock() + Math.max(quantity, 0));
        bookRepository.save(book);
    }

    public BookForm toForm(Book book) {
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setBookType(book instanceof PrintedBook ? "PRINTED" : "DIGITAL");
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setCategory(book.getCategory());
        form.setIsbn(book.getIsbn());
        form.setDescription(book.getDescription());
        form.setPrice(book.getPrice());
        form.setStock(book.getStock());
        form.setRating(book.getRating());
        form.setFeatured(book.isFeatured());
        form.setAccentColor(book.getAccentColor());
        form.setCoverTone(book.getCoverTone());
        if (book instanceof PrintedBook printedBook) {
            form.setPageCount(printedBook.getPageCount());
            form.setShelfCode(printedBook.getShelfCode());
        }
        if (book instanceof DigitalBook digitalBook) {
            form.setFileFormat(digitalBook.getFileFormat());
            form.setFileSizeMb(digitalBook.getFileSizeMb());
        }
        return form;
    }

    public BookForm emptyForm() {
        BookForm form = new BookForm();
        form.setBookType("PRINTED");
        form.setAccentColor("#204B57");
        form.setCoverTone("#F7B267");
        form.setRating(4.7);
        form.setStock(10);
        form.setPageCount(250);
        form.setFileSizeMb(5.0);
        return form;
    }

    public List<String> categories() {
        return bookRepository.findAllBooks().stream()
                .map(Book::getCategory)
                .distinct()
                .sorted()
                .toList();
    }

    public long count() {
        return bookRepository.count();
    }

    public int totalStock() {
        return bookRepository.findAllBooks().stream().mapToInt(Book::getStock).sum();
    }

    private PrintedBook buildPrintedBook(BookForm form) {
        PrintedBook existing = form.getId() == null || form.getId().isBlank()
                ? null
                : (PrintedBook) requireById(form.getId());
        return new PrintedBook(
                existing == null ? IdGenerator.create("BOO") : existing.getId(),
                form.getTitle().trim(),
                form.getAuthor().trim(),
                form.getCategory().trim(),
                form.getIsbn().trim(),
                form.getDescription().trim(),
                form.getPrice(),
                form.getStock(),
                form.getRating(),
                form.isFeatured(),
                form.getAccentColor().trim(),
                form.getCoverTone().trim(),
                form.getPageCount(),
                form.getShelfCode().trim()
        );
    }

    private DigitalBook buildDigitalBook(BookForm form) {
        DigitalBook existing = form.getId() == null || form.getId().isBlank()
                ? null
                : (DigitalBook) requireById(form.getId());
        return new DigitalBook(
                existing == null ? IdGenerator.create("BOO") : existing.getId(),
                form.getTitle().trim(),
                form.getAuthor().trim(),
                form.getCategory().trim(),
                form.getIsbn().trim(),
                form.getDescription().trim(),
                form.getPrice(),
                form.getStock(),
                form.getRating(),
                form.isFeatured(),
                form.getAccentColor().trim(),
                form.getCoverTone().trim(),
                form.getFileFormat().trim(),
                form.getFileSizeMb()
        );
    }

    private void validateTypeSpecificFields(BookForm form) {
        String type = normalize(form.getBookType());
        if ("printed".equals(type) && (form.getPageCount() == null || form.getShelfCode() == null || form.getShelfCode().isBlank())) {
            throw new IllegalArgumentException("Printed books require page count and shelf code.");
        }
        if ("digital".equals(type) && (form.getFileFormat() == null || form.getFileFormat().isBlank() || form.getFileSizeMb() == null)) {
            throw new IllegalArgumentException("Digital books require file format and file size.");
        }
    }

    private void validateUniqueIsbn(String isbn, String existingBookId) {
        String normalizedIsbn = normalize(isbn);
        boolean duplicate = bookRepository.findAllBooks().stream()
                .anyMatch(book -> normalize(book.getIsbn()).equals(normalizedIsbn)
                        && !book.getId().equals(existingBookId));
        if (duplicate) {
            throw new IllegalArgumentException("ISBN is already used by another book.");
        }
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
