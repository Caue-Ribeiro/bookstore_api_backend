package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.AuthorDTO;
import com.caue.bookstore.services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "api/authors")
public class AuthorController {

    private AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {

        AuthorDTO author = service.getAuthorById(id);

        return ResponseEntity.ok(author);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {

        List<AuthorDTO> authors = service.getAuthors();

        return ResponseEntity.ok(authors);
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> insert(@RequestBody AuthorDTO dto) {

        dto = service.insertNewAuthor(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthorById(@PathVariable Long id) {
        service.deleteAuthorById(id);

        return ResponseEntity.noContent().build();
    }
}
