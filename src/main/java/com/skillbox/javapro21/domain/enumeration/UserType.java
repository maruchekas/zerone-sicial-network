package com.skillbox.javapro21.domain.enumeration;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Set.of;

public enum UserType {
    USER(of(Permission.USER)),
    MODERATOR(of(Permission.USER, Permission.MODERATOR)),
    ADMIN(of(Permission.USER, Permission.MODERATOR, Permission.ADMIN));

    private final Set<Permission> permissions;

    UserType(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
    }
}
