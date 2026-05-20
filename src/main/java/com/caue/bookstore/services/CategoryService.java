package com.caue.bookstore.services;

import com.caue.bookstore.dto.CategoryDTO;
import com.caue.bookstore.dto.CategoryRequestDTO;
import com.caue.bookstore.entities.Category;
import com.caue.bookstore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
    public CategoryRequestDTO insert(CategoryRequestDTO dto) {
            List<Category> entity = new ArrayList<>();

            dto.getCategoryList().forEach(categoryDTO -> entity.add(new Category(null, categoryDTO.getType())));

            dto.getCategoryList().clear();
            repository.saveAll(entity).forEach(category -> dto.getCategoryList().add(new CategoryDTO(category)));



        return dto;
    }
}
