package com.caue.bookstore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserRole {

    @JsonProperty("admin")
    ROLE_ADMIN,
    @JsonProperty("client")
    ROLE_CLIENT
}
