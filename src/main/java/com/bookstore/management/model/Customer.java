package com.bookstore.management.model;

import java.time.LocalDateTime;

public class Customer extends AppUser {

    private String favoriteGenre;
    private String loyaltyTier;
    private String shippingAddress;

    public Customer() {
    }

    public Customer(String id, String fullName, String email, String phone, String password, boolean active,
                    LocalDateTime createdAt, String favoriteGenre, String loyaltyTier, String shippingAddress) {
        super(id, fullName, email, phone, password, active, createdAt);
        this.favoriteGenre = favoriteGenre;
        this.loyaltyTier = loyaltyTier;
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String getRoleName() {
        return "Customer";
    }

    @Override
    public boolean canManageStore() {
        return false;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
