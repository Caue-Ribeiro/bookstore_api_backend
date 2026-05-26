package com.caue.bookstore.controllers;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.services.CustomUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "api/users")
public class UserController {

    private CustomUserService service;

    public UserController(CustomUserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserDTO> insertNewUser(@RequestBody UserDTO dto){

        dto = service.insert(dto);


        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

    }
}
