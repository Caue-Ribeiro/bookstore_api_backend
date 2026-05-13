package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<BookResponseDTO> insert(@RequestBody BookRequestDTO dto) {

        BookResponseDTO book = service.insertNewBook(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(book.getId()).toUri();

        return ResponseEntity.created(uri).body(book);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookResponseDTO> editBook(@PathVariable UUID id, @RequestBody Map<String, Object> update) {
        String key = update.keySet().iterator().next();
        Object value = update.values().iterator().next();

        BookResponseDTO book = service.updateBook(id, key, value);

        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {

        service.deleteBookById(id);

        return ResponseEntity.noContent().build();
    }
}
