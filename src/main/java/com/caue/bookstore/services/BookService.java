package com.caue.bookstore.services;

import com.caue.bookstore.AIService.AssistantService;
import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.entities.Book;
import com.caue.bookstore.entities.Category;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class BookService {

    private final BookRepository repository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final AssistantService assistantService;
    private final ModelMapper modelMapper;

    public BookService(BookRepository repository, CategoryRepository categoryRepository, AuthorRepository authorRepository, AssistantService assistantService, ModelMapper modelMapper) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.assistantService = assistantService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(UUID id) {


        List<BookProjection> entity = repository.findBookById(id);

        return new BookResponseDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> getAllBooks(@RequestParam Pageable pageable) {

        return repository.findAll(pageable).map(BookResponseDTO::new);
    }

    @Transactional
    public BookResponseDTO insertNewBook(BookRequestDTO dto) {

        Book entity = new Book();

        BookRequestDTO bookRequestDTO = assistantService.extractBookData(dto.getTitle());

        entity.setTitle(dto.getTitle());
        entity.setIsbn(bookRequestDTO.getIsbn());
        entity.setReleaseDate(bookRequestDTO.getReleaseDate());
        entity.setStock(dto.getStock());
        entity.setPrice(dto.getPrice());
        entity.setCoverImageUrl(dto.getCoverImageUrl());

        entity.setDescription(bookRequestDTO.getDescription());

        List<Category> categories = categoryRepository.findAllById(dto.getCategoriesIds());

        List<Author> authors = authorRepository.findAllById(dto.getAuthorsIds());

        entity.getCategories().addAll(categories);
        entity.getAuthors().addAll(authors);

        entity = repository.saveAndFlush(entity);

        return new BookResponseDTO(entity);
    }


    @Transactional
    public BookResponseDTO updateBook(UUID id, String key, Object value) {

        Book book = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Field field = ReflectionUtils.findField(Book.class, key);
        try {
            if (field != null) {
                field.setAccessible(true);
                Object convertedField = modelMapper.map(value, field.getType());
                ReflectionUtils.setField(field, book, convertedField);
            }
            return modelMapper.map(book, BookResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void deleteBookById(UUID id) {

        repository.deleteById(id);
    }
}
