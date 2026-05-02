package com.bookstore.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {

    private String bookId;
    private String title;
    private String formatLabel;
    private int quantity;
    private double unitPrice;

    public OrderItem() {
    }

    public OrderItem(String bookId, String title, String formatLabel, int quantity, double unitPrice) {
        this.bookId = bookId;
        this.title = title;
        this.formatLabel = formatLabel;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @JsonIgnore
    public double getLineTotal() {
        return unitPrice * quantity;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormatLabel() {
        return formatLabel;
    }

    public void setFormatLabel(String formatLabel) {
        this.formatLabel = formatLabel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
