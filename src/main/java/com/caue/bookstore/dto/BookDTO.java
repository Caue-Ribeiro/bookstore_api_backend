package com.caue.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public BookDTO(UUID id, String title, String isbn, LocalDate releaseDate, Integer stock, BigDecimal price, String description, String coverImageUrl) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
    }


    public BookDTO(String title, String isbn, LocalDate releaseDate, Integer stock, BigDecimal price, String description, String coverImageUrl) {
        this.title = title;
        this.isbn = isbn;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
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
