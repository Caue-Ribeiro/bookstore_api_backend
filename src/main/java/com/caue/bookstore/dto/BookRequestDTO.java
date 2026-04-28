package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BookRequestDTO {

    private String title;

    private String isbn;

    private LocalDate releaseDate;

    private Integer stock;

    private BigDecimal price;

    private String description;

    private Set<Long> categoriesIds = new HashSet<>();

    private  Set<Long> authorsIds = new HashSet<>();

    public BookRequestDTO() {
    }

    public BookRequestDTO(Book entity) {
        title = entity.getTitle();
        isbn = entity.getIsbn();
        releaseDate = entity.getReleaseDate();
        stock = entity.getStock();
        price = entity.getPrice();
        description = entity.getDescription();

        entity.getAuthors().forEach(author -> authorsIds.add(author.getId()));

        entity.getCategories().forEach(category -> categoriesIds.add(category.getId()));
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

    public Set<Long> getCategoriesIds() {
        return categoriesIds;
    }

    public void setCategoriesIds(Set<Long> categoriesIds) {
        this.categoriesIds = categoriesIds;
    }

    public Set<Long> getAuthorsIds() {
        return authorsIds;
    }

    public void setAuthorsIds(Set<Long> authorsIds) {
        this.authorsIds = authorsIds;
    }
}
