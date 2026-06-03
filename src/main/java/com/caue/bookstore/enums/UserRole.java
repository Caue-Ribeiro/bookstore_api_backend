package com.caue.bookstore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public enum UserRole {

    @JsonProperty("admin")
    ADMIN(Set.of(Permission.READ,Permission.WRITE,Permission.DELETE)),
    @JsonProperty("client")
    CLIENT(Set.of(Permission.READ));


    private Set<Permission> permissions = new HashSet<>();

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
