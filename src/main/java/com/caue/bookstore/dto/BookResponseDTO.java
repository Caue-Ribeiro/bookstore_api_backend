package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;
import com.caue.bookstore.projections.BookProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class BookResponseDTO {

    private UUID id;

    private String title;

    private String isbn;

    private LocalDate releaseDate;

    private Integer stock;

    private BigDecimal price;

    private String description;

    private final Map<Long, AuthorDTO> authors = new HashMap<>();

    private final List<CategoryDTO> categories = new ArrayList<>();

    public BookResponseDTO() {
    }

    public BookResponseDTO(List<BookProjection> entity) {
        id = entity.getFirst().getBookId();
        title = entity.getFirst().getTitle();
        isbn = entity.getFirst().getIsbn();
        releaseDate = entity.getFirst().getReleaseDate();
        stock = entity.getFirst().getStock();
        price = entity.getFirst().getPrice();
        description = entity.getFirst().getDescription();


        for (BookProjection bk : entity) {
            // 1. Check if category is not null (handles LEFT JOIN nulls)
            if (bk.getCategoryId() != null) {
                // 2. Prevent duplicates caused by multiple SQL rows
                boolean categoryExists = categories.stream()
                        .anyMatch(c -> c.getId().equals(bk.getCategoryId()));

                if (!categoryExists) {
                    categories.add(new CategoryDTO(bk.getCategoryId(), bk.getType()));
                }
            }
        }

        for (BookProjection bk : entity) {
            // 1. Check if author is not null BEFORE putting it in the map!
            if (bk.getAuthorId() != null) {
                if (!authors.containsKey(bk.getAuthorId())) {
                    authors.put(bk.getAuthorId(), new AuthorDTO(bk.getAuthorId(), bk.getAuthorName(), bk.getAuthorLastName()));
                }
            }
        }


    }

    public BookResponseDTO(Book entity) {
        id = entity.getId();
        title = entity.getTitle();
        isbn = entity.getIsbn();
        releaseDate = entity.getReleaseDate();
        stock = entity.getStock();
        price = entity.getPrice();
        description = entity.getDescription();

        entity.getCategories().forEach(category -> categories.add(new CategoryDTO(category)));

        entity.getAuthors().forEach(author -> authors.put(author.getId(), new AuthorDTO(author)));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<Long, AuthorDTO> getAuthors() {
        return authors;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
