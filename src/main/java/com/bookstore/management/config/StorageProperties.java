package com.bookstore.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bookstore.storage")
public class StorageProperties {

    private String root = "storage";
    private String customersFile = "customers.txt";
    private String adminsFile = "admins.txt";
    private String booksFile = "books.txt";
    private String cartsFile = "carts.txt";
    private String ordersFile = "orders.txt";
    private String auditFile = "audit-log.txt";

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getCustomersFile() {
        return customersFile;
    }

    public void setCustomersFile(String customersFile) {
        this.customersFile = customersFile;
    }

    public String getAdminsFile() {
        return adminsFile;
    }

    public void setAdminsFile(String adminsFile) {
        this.adminsFile = adminsFile;
    }

    public String getBooksFile() {
        return booksFile;
    }

    public void setBooksFile(String booksFile) {
        this.booksFile = booksFile;
    }

    public String getCartsFile() {
        return cartsFile;
    }

    public void setCartsFile(String cartsFile) {
        this.cartsFile = cartsFile;
    }

    public String getOrdersFile() {
        return ordersFile;
    }

    public void setOrdersFile(String ordersFile) {
        this.ordersFile = ordersFile;
    }

    public String getAuditFile() {
        return auditFile;
    }

    public void setAuditFile(String auditFile) {
        this.auditFile = auditFile;
    }
}
