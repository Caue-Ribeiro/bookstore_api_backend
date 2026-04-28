package com.caue.bookstore.services;

import com.caue.bookstore.dto.AuthorDTO;
import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.dto.CategoryDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.entities.Book;
import com.caue.bookstore.entities.Category;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository repository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;


    public BookService(BookRepository repository, CategoryRepository categoryRepository, AuthorRepository authorRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(UUID id){

        List<BookProjection> entity = repository.findBookById(id);

        return new BookResponseDTO(entity);
    }

    @Transactional
    public BookResponseDTO insertNewBook(BookRequestDTO dto){

        Book entity = new Book();

        entity.setTitle(dto.getTitle());
        entity.setIsbn(dto.getIsbn());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setStock(dto.getStock());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());

        for (CategoryDTO cat: dto.getCategories()){
            Category category = new Category();
            category.setId(cat.getId());
            entity.getCategories().add(category);
        }

        for (AuthorDTO aut: dto.getAuthors()){
          Author author = new Author();
          author.setId(aut.getId());
          entity.getAuthors().add(author);
        }

       entity = repository.saveAndFlush(entity);

       return new BookResponseDTO(entity);
    }
}
