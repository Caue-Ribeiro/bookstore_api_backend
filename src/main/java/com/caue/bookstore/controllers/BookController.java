package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value= "api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable UUID id){

        BookResponseDTO book = service.getBookById(id);

        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> insert(@RequestBody BookRequestDTO dto){

        BookResponseDTO book = service.insertNewBook(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(book.getId()).toUri();

        return ResponseEntity.created(uri).body(book);
    }
}
