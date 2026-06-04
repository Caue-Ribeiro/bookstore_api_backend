package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping(value = "api/users")
public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserDTO> insertNewUser(@RequestBody UserDTO dto) {

        dto = service.insert(dto);


        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = service.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> user = service.getAllUsers(pageable);

        return ResponseEntity.ok(user);
    }



    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<UserDTO> editUser(@PathVariable UUID id, @RequestBody UserDTO dto) {

        UserDTO user = service.editUser(id, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityChecker.isUserOwner(authentication,#id)")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
