package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Category;
import com.caue.bookstore.enums.CategoryType;
import jakarta.validation.constraints.NotNull;

public class CategoryDTO {

    private Long id;

    @NotNull(message = "Category type is required and must be valid.")
    private CategoryType type;


    public CategoryDTO() {
    }

    public CategoryDTO(Long id, CategoryType type) {
        this.id = id;
        this.type = type;
    }

    public CategoryDTO(Category entity) {
        id = entity.getId();
        type = entity.getType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }
}
