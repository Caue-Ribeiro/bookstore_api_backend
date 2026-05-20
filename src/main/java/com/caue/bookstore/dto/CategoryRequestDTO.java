package com.caue.bookstore.dto;

import com.caue.bookstore.enums.CategoryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CategoryRequestDTO {



    @NotEmpty(message = "Category list must not be empty.")
    private List<@Valid CategoryDTO> categoryList;


    public CategoryRequestDTO(List<@Valid CategoryDTO> categoryList) {
        this.categoryList = categoryList;
    }

    public void addCategory(Long id, CategoryType type) {
        categoryList.add(new CategoryDTO(id, type));
    }

    public List<@Valid CategoryDTO> getCategoryList() {
        return categoryList;
    }

}
