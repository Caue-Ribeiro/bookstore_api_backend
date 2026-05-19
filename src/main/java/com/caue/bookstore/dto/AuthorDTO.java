package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class AuthorDTO {
    private Long id;

    @Size(min = 2, max = 100, message = "Name must be between 2 to 100 characters.")
    @NotBlank(message = "Name must not be blank.")
    private String name;

    @Size(min = 2, max = 100, message = "Last name must be between 2 to 100 characters.")
    @NotBlank(message = "Last name must not be blank.")
    private String lastName;

    public AuthorDTO() {

    }

    public AuthorDTO(Long id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public AuthorDTO(Author entity) {
        id = entity.getId();
        name = entity.getName();
        lastName = entity.getLastName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
