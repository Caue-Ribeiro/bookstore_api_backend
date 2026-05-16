package com.caue.bookstore.services;

import com.caue.bookstore.dto.CategoryDTO;
import com.caue.bookstore.entities.Category;
import com.caue.bookstore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repository;


    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {

        List<Category> categories = repository.findAll();

        return categories.stream().map(CategoryDTO::new).toList();
    }

    @Transactional
    public List<CategoryDTO> insert(List<CategoryDTO> dto) {
        List<Category> categoryList = new ArrayList<>();
        dto.forEach(categoryDTO -> categoryList.add(new Category(null, categoryDTO.getType())));

        dto.clear();
        repository.saveAll(categoryList).forEach(category -> dto.add(new CategoryDTO(category)));

        return dto;
    }
}
