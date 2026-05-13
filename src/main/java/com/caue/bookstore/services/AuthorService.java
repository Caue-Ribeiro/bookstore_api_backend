package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuthorDTO;
import com.caue.bookstore.dto.AuthorResponseDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.AuthorProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository repository;
    private ModelMapper modelMapper;

    public AuthorService(AuthorRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
   public AuthorDTO getAuthorById(Long id){
        List<AuthorProjection> author = repository.findAuthorById(id);

        return new AuthorResponseDTO(author);


    }
}
