package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.entities.BookByCategory;
import com.caue.bookstore.entities.BookEvent;
import com.caue.bookstore.entities.ReaderProfileResponse;
import com.caue.bookstore.enums.CategoryType;
import com.caue.bookstore.services.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping(value = "api/books")
public class BookController {

    private final BookService service;


    public BookController(BookService service) {
        this.service = service;

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable UUID id) {

        BookResponseDTO book = service.getBookById(id);

        return ResponseEntity.ok(book);
    }


    @GetMapping
    ResponseEntity<Page<BookResponseDTO>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDTO> books = service.getAllBooksPaginated(pageable);

        return ResponseEntity.ok(books);
    }

    @GetMapping("/list")
    ResponseEntity<List<BookResponseDTO>> getAllBooks() {

        List<BookResponseDTO> books = service.getAllBooks();

        return ResponseEntity.ok(books);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<BookResponseDTO>> getBooksByCategoryPaginated(@PathVariable String category,
                                                 @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "12") int size){

        Pageable pageable = PageRequest.of(page, size);
       Page<BookResponseDTO> bookResponse= service.getBooksByCategoryPaginated(category, pageable);

        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping(value = "/events")
    public ResponseEntity<List<BookEvent>> getBookEvents(){
       List<BookEvent> bookEvents =  service.getBookEvents();

       return ResponseEntity.ok(bookEvents);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> insert(@Valid @RequestBody BookRequestDTO dto) {

        BookResponseDTO book = service.insertNewBook(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(book.getId()).toUri();

        return ResponseEntity.created(uri).body(book);
    }

    @PostMapping("/reader-discovery")
    public ResponseEntity<ReaderProfileResponse> readingAIRecommender(@RequestBody Map<String,String> payload){

        String userInput = payload.get("userInput");

       ReaderProfileResponse readerProfileResponse= service.readingAIRecommender(userInput);

       return ResponseEntity.ok(readerProfileResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookResponseDTO>> searchBooks(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDTO> books = service.searchBooks(q, pageable);

        return ResponseEntity.ok(books);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> editBook(@PathVariable UUID id,@Valid @RequestBody Map<String, Object> update) {
        Set<String> key = update.keySet();


        BookResponseDTO book = service.updateBook(id, key, update);

        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {

        service.deleteBookById(id);

        return ResponseEntity.noContent().build();
    }
}
