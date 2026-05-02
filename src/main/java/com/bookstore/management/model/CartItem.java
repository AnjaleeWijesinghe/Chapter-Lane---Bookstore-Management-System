package com.bookstore.management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItem {

    private String bookId;
    private String title;
    private String formatLabel;
    private String coverTone;
    private double unitPrice;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String bookId, String title, String formatLabel, String coverTone, double unitPrice, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.formatLabel = formatLabel;
        this.coverTone = coverTone;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
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

    public String getCoverTone() {
        return coverTone;
    }

    public void setCoverTone(String coverTone) {
        this.coverTone = coverTone;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
