package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuthorDTO;
import com.caue.bookstore.dto.AuthorResponseDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.entities.WikipediaSummary;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private AuthorService service;

    @Test
    void shouldGetAuthorByIdWithBooks() {
        BookProjection projection = mock(BookProjection.class);
        when(projection.getAuthorId()).thenReturn(1L);
        when(projection.getAuthorName()).thenReturn("Fyodor");
        when(projection.getAuthorLastName()).thenReturn("Dostoevsky");

        when(repository.findAuthorById(1L)).thenReturn(List.of(projection));

        AuthorDTO result = service.getAuthorById(1L);

        assertInstanceOf(AuthorResponseDTO.class, result);
        assertEquals(1L, result.getId());
        assertEquals("Fyodor", result.getName());
        assertEquals("Dostoevsky", result.getLastName());
        verify(repository).findAuthorById(1L);
    }

    @Test
    void shouldThrowWhenAuthorByIdNotFound() {
        when(repository.findAuthorById(99L)).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getAuthorById(99L));

        assertEquals("Author not found.", exception.getMessage());
        verify(repository).findAuthorById(99L);
    }

    @Test
    void shouldReturnAllAuthors() {
        Author first = new Author(1L, "Fyodor", "Dostoevsky");
        Author second = new Author(2L, "George", "Orwell");

        when(repository.findAll()).thenReturn(List.of(first, second));

        List<AuthorDTO> result = service.getAuthors();

        assertEquals(2, result.size());
        assertEquals("Fyodor", result.getFirst().getName());
        assertEquals("Orwell", result.get(1).getLastName());
        verify(repository).findAll();
    }

    @Test
    void shouldSearchAuthorsWithPageable() {
        Author author = new Author(1L, "George", "Orwell");
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Author> entityPage = new PageImpl<>(List.of(author), pageable, 1);

        when(repository.searchAuthor("geo", pageable)).thenReturn(entityPage);

        Page<AuthorDTO> result = service.searchAuthor("geo", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("George", result.getContent().getFirst().getName());
        verify(repository).searchAuthor("geo", pageable);
    }

    @Test
    void shouldInsertNewAuthor() {
        AuthorDTO request = new AuthorDTO(null, "Franz", "Kafka");
        Author mappedEntity = new Author(null, "Franz", "Kafka");
        Author savedEntity = new Author(10L, "Franz", "Kafka");
        AuthorDTO response = new AuthorDTO(10L, "Franz", "Kafka");

        when(modelMapper.map(request, Author.class)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, AuthorDTO.class)).thenReturn(response);

        AuthorDTO result = service.insertNewAuthor(request);

        assertEquals(10L, result.getId());
        assertEquals("Franz", result.getName());
        verify(modelMapper).map(request, Author.class);
        verify(repository).save(mappedEntity);
        verify(modelMapper).map(savedEntity, AuthorDTO.class);
    }

    @Test
    void shouldDeleteAuthorById() {
        when(repository.existsById(3L)).thenReturn(true);

        service.deleteAuthorById(3L);

        verify(repository).existsById(3L);
        verify(repository).deleteById(3L);
    }

    @Test
    void shouldThrowWhenDeleteAuthorByIdNotFound() {
        when(repository.existsById(3L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.deleteAuthorById(3L));

        assertEquals("Author not found.", exception.getMessage());
        verify(repository).existsById(3L);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void shouldGetAuthorSummaryReplacingSpacesWithUnderscore() {
        WikipediaSummary summary = new WikipediaSummary();

        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("/{authorName}"), eq("George_Orwell"))).thenReturn(headersSpec);
        when(headersSpec.header(eq(HttpHeaders.USER_AGENT), anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(WikipediaSummary.class)).thenReturn(summary);

        WikipediaSummary result = service.getAuthorSummary("George Orwell");

        assertSame(summary, result);
        verify(uriSpec).uri(eq("/{authorName}"), eq("George_Orwell"));
        verify(headersSpec).header(eq(HttpHeaders.USER_AGENT), eq("BookStoreApp/1.0 (caueribeiro.dev@gmail.com)"));
        verify(responseSpec).body(WikipediaSummary.class);
    }
}
