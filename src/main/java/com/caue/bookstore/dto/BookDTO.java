package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;
import com.caue.bookstore.projections.BookProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BookDTO {

    private UUID id;

    private String title;

    private String isbn;

    private LocalDate releaseDate;

    private Integer stock;

    private BigDecimal price;

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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
