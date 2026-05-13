package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;
import com.caue.bookstore.projections.BookProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class BookResponseDTO extends BookDTO {


    private List<AuthorDTO> authors = new ArrayList<>();

    private List<CategoryDTO> categories = new ArrayList<>();

    public BookResponseDTO() {
    }

    public BookResponseDTO(UUID id, String title, String isbn, LocalDate releaseDate, Integer stock, BigDecimal price, String description, String coverImageUrl) {
        super(id, title, isbn, releaseDate, stock, price, description, coverImageUrl);
    }

    public BookResponseDTO(List<BookProjection> entity) {

        super(entity.getFirst().getBookId(), entity.getFirst().getTitle(), entity.getFirst().getIsbn(), entity.getFirst().getReleaseDate(), entity.getFirst().getStock(), entity.getFirst().getPrice(), entity.getFirst().getDescription(), entity.getFirst().getCoverImageUrl());


        for (BookProjection bk : entity) {
            if (bk.getCategoryId() != null) {
                boolean categoryExists = categories.stream().anyMatch(c -> c.getId().equals(bk.getCategoryId()));

                if (!categoryExists) {
                    categories.add(new CategoryDTO(bk.getCategoryId(), bk.getType()));
                }
            }
        }

        Map<Long, AuthorDTO> authorDTOMap = new HashMap<>();
        for (BookProjection bk : entity) {
            if (bk.getAuthorId() != null) {
                if (!authorDTOMap.containsKey(bk.getAuthorId())) {
                    authorDTOMap.put(bk.getAuthorId(), new AuthorDTO(bk.getAuthorId(), bk.getAuthorName(), bk.getAuthorLastName()));
                }
            }
        }
        authors = new ArrayList<>(authorDTOMap.values());


    }

    public BookResponseDTO(Book entity) {

        super(entity.getId(), entity.getTitle(), entity.getIsbn(), entity.getReleaseDate(), entity.getStock(), entity.getPrice(), entity.getDescription(), entity.getCoverImageUrl());
        entity.getCategories().forEach(category -> categories.add(new CategoryDTO(category)));

        entity.getAuthors().forEach(author -> authors.add(new AuthorDTO(author)));
    }


    public List<AuthorDTO> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDTO> authors) {
        this.authors = authors;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }
}
