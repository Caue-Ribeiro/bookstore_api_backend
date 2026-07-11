package com.caue.bookstore.services;

import com.caue.bookstore.AIService.AssistantService;
import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.entities.*;
import com.caue.bookstore.enums.CategoryType;
import com.caue.bookstore.enums.OrderStatus;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.CategoryRepository;
import com.caue.bookstore.repositories.OrderRepository;
import org.jspecify.annotations.NonNull;
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

    private final String EXCEPTION_MESSAGE = "Book not found.";

    public BookService(BookRepository repository, CategoryRepository categoryRepository, AuthorRepository authorRepository, AssistantService assistantService, ModelMapper modelMapper) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.assistantService = assistantService;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(UUID id) {

        try {
            List<BookProjection> entity = repository.findBookById(id);

            return new BookResponseDTO(entity);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException(EXCEPTION_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks(){
       List<Book> books= repository.findAll();

      return books.stream().map(BookResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<@NonNull BookResponseDTO> getAllBooksPaginated(Pageable pageable) {

        return repository.findAll(pageable).map(BookResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public List<BookEvent> getBookEvents(){
        return  repository.findAllEvents();

    }

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> getBooksByCategoryPaginated(String category, Pageable pageable){
        CategoryType categoryEnum = CategoryType.valueOf(category.toUpperCase());
   return repository.findByCategories_Type(categoryEnum,pageable).map(BookResponseDTO::new);

    }

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> searchBooks(String query, Pageable pageable) {
        return repository.searchBooks(query, pageable).map(BookResponseDTO::new);
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
    public BookResponseDTO updateBook(UUID id, Set<String> key, Map<String, Object> value) {

        Book book = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EXCEPTION_MESSAGE));

        if (key.contains("authors")) {

            @SuppressWarnings("unchecked") List<Long> authorsIds = (List<Long>) value.get("authors");

            Set<Author> authors = new HashSet<>(authorRepository.findAllById(authorsIds));

            book.setAuthors(authors);

            value.remove("authors");
        }
        if (key.contains("categories")) {

            @SuppressWarnings("unchecked") List<Long> categoriesIds = (List<Long>) value.get("categories");

            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoriesIds));

            book.setCategories(categories);

            value.remove("categories");
        }

        if (!value.isEmpty()) {
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                String attributeKey = entry.getKey();
                Field field = ReflectionUtils.findField(Book.class, attributeKey);
                try {
                    if (field != null) {
                        field.setAccessible(true);
                        Object convertedField = modelMapper.map(entry.getValue(), field.getType());
                        ReflectionUtils.setField(field, book, convertedField);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to update field: " + attributeKey, e);
                }
            }
            value.clear();
        }
        return modelMapper.map(book, BookResponseDTO.class);
    }


    public void deleteBookById(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(EXCEPTION_MESSAGE);
        }

        repository.deleteById(id);
    }

    public ReaderProfileResponse readingAIRecommender(String userInput){

       ReaderProfileResponse readerProfileResponse= assistantService.bookAdviser(userInput);

       return readerProfileResponse;


    }



}
