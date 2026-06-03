package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //todo: test all these new methods

    //TESTED
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = service.getUserById(id);

        return ResponseEntity.ok(user);
    }

    //TESTED
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> user = service.getAllUsers(pageable);

        return ResponseEntity.ok(user);
    }



    //TESTED
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> editUser(@PathVariable UUID id, @RequestBody UserDTO dto) {

        UserDTO user = service.editUser(id, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    //TESTED
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
