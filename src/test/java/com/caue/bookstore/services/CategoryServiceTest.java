package com.caue.bookstore.services;


import com.caue.bookstore.dto.CategoryDTO;
import com.caue.bookstore.dto.CategoryRequestDTO;
import com.caue.bookstore.entities.Category;
import com.caue.bookstore.enums.CategoryType;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    private Category category = null;

    @BeforeEach
    void defaultValues(){
        category = new Category();

        category.setId(1L);
        category.setType(CategoryType.CLASSICS);
    }


    @Test
    void shouldGetAllCategories(){
       when(categoryRepository.findAll()) .thenReturn(List.of(category));

      List<CategoryDTO> categories =  categoryService.getAllCategories();

      assertNotNull(categories);

      verify(categoryRepository,times(1)).findAll();

    }

    @Test
    void shouldSearchCategories(){
        String searchVal = "Category";

        when(categoryRepository.searchCategories(searchVal, PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of(category)));

        Page<CategoryDTO> result =  categoryService.searchCategory(searchVal,PageRequest.of(0,10));

        assertNotNull(result);

        verify(categoryRepository,times(1)).searchCategories(searchVal, PageRequest.of(0,10));
    }

    @Test
    void shouldInsertNewCategory(){
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);

        CategoryDTO categoryDTO = new CategoryDTO(1L, CategoryType.CLASSICS);

        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        categoryDTOList.add(categoryDTO);


        when(categoryRepository.saveAll(anyList())).thenReturn(categoryList);

        CategoryRequestDTO result =  categoryService.insert(new CategoryRequestDTO(categoryDTOList));

        assertNotNull(result);

        verify(categoryRepository,times(1)).saveAll(anyList());

    }

    @Test
    void shouldDeleteCategory(){

       doNothing().when(categoryRepository).deleteById(category.getId());

       when(categoryRepository.existsById(category.getId())).thenReturn(true);

       categoryService.deleteCategoryById(category.getId());

       verify(categoryRepository,times(1)).deleteById(category.getId());
    }

    @Test
    void shouldDeleteCategoryFail(){

      ResourceNotFoundException exception= assertThrows(ResourceNotFoundException.class,
                ()-> categoryService.deleteCategoryById(category.getId()));

      assertEquals("Category not found.",exception.getMessage());

    }

    @Test
    void shouldDeleteCategoryThrowException(){

        when(categoryRepository.existsById(category.getId())).thenReturn(true);

        doThrow(new DataIntegrityViolationException("Database constraint violation")).when(categoryRepository).deleteById(category.getId());

        DatabaseException exception= assertThrows(DatabaseException.class,
                ()-> categoryService.deleteCategoryById(category.getId()));

        assertEquals("Data integrity violated",exception.getMessage());


        verify(categoryRepository, times(1)).existsById(category.getId());
        verify(categoryRepository, times(1)).deleteById(category.getId());
        verify(categoryRepository, never()).flush();

    }


}
