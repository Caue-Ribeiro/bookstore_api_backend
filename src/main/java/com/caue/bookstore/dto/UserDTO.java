package com.caue.bookstore.dto;

import com.caue.bookstore.entities.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private String name;
    private String lastName;
    private String email;
    private LocalDate birthdate;
    private String password;

    private List<RoleDTO> roles = new ArrayList<>();


    public UserDTO() {
    }

    public UserDTO(String name, String lastName, String email, LocalDate birthdate, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.password = password;

    }

    public UserDTO(User entity) {
        name = entity.getName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        birthdate = entity.getBirthdate();
        password = entity.getPassword();

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

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
