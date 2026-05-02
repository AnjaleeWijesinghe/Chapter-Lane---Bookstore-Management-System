package com.bookstore.management.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckoutForm {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String note;

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
