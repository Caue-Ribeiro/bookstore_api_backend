package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuthorDTO;
import com.caue.bookstore.dto.AuthorResponseDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public AuthorDTO getAuthorById(Long id) {
        List<BookProjection> author = repository.findAuthorById(id);

        return new AuthorResponseDTO(author);


    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> getAuthors() {

        List<Author> entityList = repository.findAll();

        List<AuthorDTO> authorsDTO = new ArrayList<>();

        entityList.forEach(author -> authorsDTO.add(new AuthorDTO(author.getId(), author.getName(), author.getLastName())));

        return authorsDTO;
    }



    @Transactional
    public AuthorDTO insertNewAuthor(AuthorDTO dto) {

        Author author = modelMapper.map(dto, Author.class);

        author = repository.save(author);

        return modelMapper.map(author, AuthorDTO.class);

    }


    @Transactional
    public void deleteAuthorById(Long id){
        repository.deleteById(id);

    }
}
