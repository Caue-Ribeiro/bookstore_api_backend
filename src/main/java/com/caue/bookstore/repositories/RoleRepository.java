package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.Role;
import com.caue.bookstore.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(UserRole authority);
}
