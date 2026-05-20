package com.bookstore.management.model;

import java.time.LocalDateTime;

/**
 * Customer entity class
 *
 * This class extends AppUser, meaning it inherits all common user properties
 * like id, fullName, email, phone, password, active status, and createdAt.
 *
 * Customer adds extra fields specific to bookstore customers.
 */
public class Customer extends AppUser {

    // Customer-specific attributes

    private String favoriteGenre;     // Customer's preferred book genre
    private String loyaltyTier;       // Loyalty level (e.g., Silver, Gold, Platinum)
    private String shippingAddress;   // Delivery address for orders

    /**
     * Default constructor
     * Required for frameworks (like Jackson) and object creation without values
     */
    public Customer() {
    }

    /**
     * Full constructor for creating a complete Customer object
     */
    public Customer(
            String id,
            String fullName,
            String email,
            String phone,
            String password,
            boolean active,
            LocalDateTime createdAt,
            String favoriteGenre,
            String loyaltyTier,
            String shippingAddress
    ) {
        // Call parent constructor to initialize common user fields
        super(id, fullName, email, phone, password, active, createdAt);

        // Initialize Customer-specific fields
        this.favoriteGenre = favoriteGenre;
        this.loyaltyTier = loyaltyTier;
        this.shippingAddress = shippingAddress;
    }

    /**
     * Returns the role name of this user
     * Used for authorization / role-based logic
     */
    @Override
    public String getRoleName() {
        return "Customer";
    }

    /**
     * Defines whether a customer can manage the store
     * Customers are NOT allowed to manage store operations
     */
    @Override
    public boolean canManageStore() {
        return false;
    }

    // ---------------------------
    // Getters and Setters
    // ---------------------------

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