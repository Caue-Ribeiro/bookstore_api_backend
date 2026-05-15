package com.caue.bookstore.dto;


import com.caue.bookstore.projections.BookProjection;

import java.util.ArrayList;
import java.util.List;

public class AuthorResponseDTO extends  AuthorDTO{

    private List<BookDTO> books = new ArrayList<>();

    public AuthorResponseDTO(List<BookProjection> entity) {

        super(entity.getFirst().getAuthorId(),entity.getFirst().getAuthorName(),entity.getFirst().getAuthorLastName());

        entity.forEach(authorProjection -> {
            BookDTO book = new BookDTO();

            book.setId(authorProjection.getBookId());
            book.setTitle(authorProjection.getTitle());
            book.setCoverImageUrl(authorProjection.getCoverImageUrl());
            book.setDescription(authorProjection.getDescription());
            book.setIsbn(authorProjection.getIsbn());
            book.setPrice(authorProjection.getPrice());
            book.setReleaseDate(authorProjection.getReleaseDate());
            book.setStock(authorProjection.getStock());

            books.add(book);
        });
    }

    public List<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }
}
