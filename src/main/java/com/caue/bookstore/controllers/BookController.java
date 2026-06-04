package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.services.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    ResponseEntity<Page<BookResponseDTO>> getAllBooks(Pageable pageable) {
        Page<BookResponseDTO> books = service.getAllBooks(pageable);

        return ResponseEntity.ok(books);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> insert(@Valid @RequestBody BookRequestDTO dto) {

        BookResponseDTO book = service.insertNewBook(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(book.getId()).toUri();

        return ResponseEntity.created(uri).body(book);
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
