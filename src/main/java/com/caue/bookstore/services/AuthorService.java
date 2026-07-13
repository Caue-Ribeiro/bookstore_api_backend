package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuthorDTO;
import com.caue.bookstore.dto.AuthorResponseDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.entities.WikipediaSummary;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository repository;
    private final ModelMapper modelMapper;
    private RestClient restClient;

    private final String EXCEPTION_MESSAGE = "Author not found.";

    public AuthorService(AuthorRepository repository, ModelMapper modelMapper, RestClient restClient) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.restClient = restClient;

    }

    @Transactional(readOnly = true)
    public AuthorDTO getAuthorById(Long id) {
        List<BookProjection> author = repository.findAuthorById(id);

        if (author == null || author.isEmpty()) {
            throw new ResourceNotFoundException(EXCEPTION_MESSAGE);
        }

        return new AuthorResponseDTO(author);
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> getAuthors() {

        List<Author> entityList = repository.findAll();

        List<AuthorDTO> authorsDTO = new ArrayList<>();

        entityList.forEach(author -> authorsDTO.add(new AuthorDTO(author.getId(), author.getName(), author.getLastName())));

        return authorsDTO;
    }

    @Transactional(readOnly = true)
    public WikipediaSummary getAuthorSummary(String authorName){
        Dotenv dotenv = Dotenv.load();

        authorName = authorName.replace(" ","_");

        return restClient.get()
                .uri("/{authorName}",authorName)
                .header(HttpHeaders.USER_AGENT, "BookStoreApp/1.0 " +"("+dotenv.get("PERSONAL_EMAIL")+")")
                .retrieve()
                .body(WikipediaSummary.class);

    }

    @Transactional(readOnly = true)
    public Page<AuthorDTO> searchAuthor(String query, Pageable pageable){
        return repository.searchAuthor(query, pageable).map(AuthorDTO::new);
    }


    @Transactional
    public AuthorDTO insertNewAuthor(AuthorDTO dto) {

        Author author = modelMapper.map(dto, Author.class);

        author = repository.save(author);

        return modelMapper.map(author, AuthorDTO.class);

    }


    @Transactional
    public void deleteAuthorById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(EXCEPTION_MESSAGE);
        }
        repository.deleteById(id);

    }
}
