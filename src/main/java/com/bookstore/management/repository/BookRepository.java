package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.bookstore.management.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository extends JsonLineFileRepository<Book> {

    public BookRepository(ObjectMapper objectMapper, StorageProperties storageProperties) {
        super(objectMapper, storageProperties, storageProperties.getBooksFile(), Book.class);
    }

    public List<Book> findAllBooks() {
        return readAll();
    }

    public Optional<Book> findById(String bookId) {
        return findOne(book -> book.getId().equals(bookId));
    }

    public Optional<Book> findByIsbn(String isbn) {
        return findOne(book -> book.getIsbn().equalsIgnoreCase(isbn));
    }

    public void save(Book book) {
        store(book, Book::getId);
    }

    public void deleteById(String bookId) {
        deleteWhere(book -> book.getId().equals(bookId));
    }

    public long count() {
        return readAll().size();
    }
}
