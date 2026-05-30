package com.caue.bookstore.dto;

import com.caue.bookstore.enums.UserRole;

public class RoleDTO {

    private Long id;

    private UserRole authority;

    public RoleDTO() {
    }


    public RoleDTO(Long id, UserRole authority) {
        this.id = id;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRole getAuthority() {
        return authority;
    }

    public void setAuthority(UserRole authority) {
        this.authority = authority;
    }
}
