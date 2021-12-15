package com.skillbox.javapro21.domain.enumeration;

public enum Permission {
    USER("user:write"),
    MODERATOR("moderator:write"),
    ADMIN("admin:write");
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

}
