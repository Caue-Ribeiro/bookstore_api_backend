package com.caue.bookstore.entities;


import com.caue.bookstore.enums.UserRole;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bs_role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole authority;


    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role() {
    }

    public Role(Long id, UserRole authority) {
        this.id = id;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Nullable String getAuthority() {
        return authority.toString();
    }

    public void setAuthority(UserRole authority) {
        this.authority = authority;
    }

    public Set<User> getUsers() {
        return users;
    }
}
