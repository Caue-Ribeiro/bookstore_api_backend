package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.CategoryDTO;
import com.caue.bookstore.dto.CategoryRequestDTO;
import com.caue.bookstore.services.CategoryService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/search")
    public ResponseEntity<Page<CategoryDTO>> searchCategories(String query, Pageable pageable){
       Page<CategoryDTO> categoryDTO = service.searchCategory(query,pageable);

       return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<@NonNull CategoryRequestDTO> insertNewCategory(@Valid @RequestBody CategoryRequestDTO dto) {

        dto = service.insert(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id){

        service.deleteCategoryById(id);

        return ResponseEntity.noContent().build();
    }
}
