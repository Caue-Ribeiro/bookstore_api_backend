package com.caue.bookstore.dto;

import com.caue.bookstore.entities.User;
import com.caue.bookstore.enums.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private LocalDate birthdate;
    private String password;

    private UserRole role;


    public UserDTO() {
    }

    public UserDTO(UUID id, String name, String lastName, String email, LocalDate birthdate, String password,
                   UserRole role) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.password = password;
        this.role = role;
    }

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        birthdate = entity.getBirthdate();
        role = entity.getRole();

    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
