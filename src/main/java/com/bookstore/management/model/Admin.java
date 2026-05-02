package com.bookstore.management.model;

import java.time.LocalDateTime;

public class Admin extends AppUser {

    private PermissionLevel permissionLevel;
    private String badgeLabel;

    public Admin() {
    }

    public Admin(String id, String fullName, String email, String phone, String password, boolean active,
                 LocalDateTime createdAt, PermissionLevel permissionLevel, String badgeLabel) {
        super(id, fullName, email, phone, password, active, createdAt);
        this.permissionLevel = permissionLevel;
        this.badgeLabel = badgeLabel;
    }

    @Override
    public String getRoleName() {
        return "Admin";
    }

    @Override
    public boolean canManageStore() {
        return true;
    }

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(PermissionLevel permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getBadgeLabel() {
        return badgeLabel;
    }

    public void setBadgeLabel(String badgeLabel) {
        this.badgeLabel = badgeLabel;
    }
}
