package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.CategoryDTO;
import com.caue.bookstore.services.CategoryService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<@NonNull List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = service.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<@NonNull List<CategoryDTO>> insertNewCategory(@Valid @RequestBody List<CategoryDTO> dto) {

        dto = service.insert(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
