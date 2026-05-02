package com.bookstore.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "bookType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PrintedBook.class, name = "PRINTED"),
        @JsonSubTypes.Type(value = DigitalBook.class, name = "DIGITAL")
})
public abstract class Book {

    private String id;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private String description;
    private double price;
    private int stock;
    private double rating;
    private boolean featured;
    private String accentColor;
    private String coverTone;

    protected Book() {
    }

    protected Book(String id, String title, String author, String category, String isbn, String description,
                   double price, int stock, double rating, boolean featured, String accentColor, String coverTone) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.rating = rating;
        this.featured = featured;
        this.accentColor = accentColor;
        this.coverTone = coverTone;
    }

    @JsonIgnore
    public abstract String getFormatLabel();

    @JsonIgnore
    public abstract String getSecondaryMetadata();

    @JsonIgnore
    public boolean isLowStock() {
        return stock <= 5;
    }

    @JsonIgnore
    public String getCoverGradient() {
        return "linear-gradient(135deg, " + accentColor + ", " + coverTone + ")";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getCoverTone() {
        return coverTone;
    }

    public void setCoverTone(String coverTone) {
        this.coverTone = coverTone;
    }
}
