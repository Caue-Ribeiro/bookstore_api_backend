package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Category;
import com.caue.bookstore.enums.CategoryType;

public class CategoryDTO {

    private Long id;

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
