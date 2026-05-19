package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;
import com.caue.bookstore.projections.BookProjection;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BookDTO {

    private UUID id;

    @Size(min = 1, max = 100, message = "Book title must be between 1 to 200 characters.")
    @NotBlank(message = "Book title must not be blank.")
    private String title;

    @Digits(integer = 13, fraction = 0, message = "ISBN must have 13 digits.")
    private Long isbn;

    private LocalDate releaseDate;

    @PositiveOrZero(message = "Stock must be ZERO or POSITIVE.")
    private Integer stock;

    @PositiveOrZero(message = "Price must be ZERO or POSITIVE.")
    private BigDecimal price;

    @NotBlank(message = "Description must not be blank.")
    @Size(min = 1, max = 500, message = "Description must have at least a character.")
    private String description;

    private String coverImageUrl;

    public BookDTO() {
    }

    public BookDTO(Book entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.isbn = entity.getIsbn();
        this.releaseDate = entity.getReleaseDate();
        this.stock = entity.getStock();
        this.price = entity.getPrice();
        this.description = entity.getDescription();
        this.coverImageUrl = entity.getCoverImageUrl();
    }

    public BookDTO(List<BookProjection> entity) {
        this.id = entity.getFirst().getBookId();
        this.title = entity.getFirst().getTitle();
        this.isbn = entity.getFirst().getIsbn();
        this.releaseDate = entity.getFirst().getReleaseDate();
        this.stock = entity.getFirst().getStock();
        this.price = entity.getFirst().getPrice();
        this.description = entity.getFirst().getDescription();
        this.coverImageUrl = entity.getFirst().getCoverImageUrl();
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

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
