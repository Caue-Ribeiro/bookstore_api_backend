package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class BookRequestDTO {

    private String title;

    private String isbn;

    private LocalDate releaseDate;

    private Integer stock;

    private BigDecimal price;

    private String description;

    private  List<CategoryDTO> categories = new ArrayList<>();

    private  List<AuthorDTO> authors = new ArrayList<>();

    public BookRequestDTO() {
    }

    public BookRequestDTO(Book entity) {
        title = entity.getTitle();
        isbn = entity.getIsbn();
        releaseDate = entity.getReleaseDate();
        stock = entity.getStock();
        price = entity.getPrice();
        description = entity.getDescription();

        entity.getAuthors().forEach(author -> authors.add(new AuthorDTO(author)));

        entity.getCategories().forEach(category -> categories.add(new CategoryDTO(category)));
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

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public List<AuthorDTO> getAuthors() {
        return authors;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public void setAuthors(List<AuthorDTO> authors) {
        this.authors = authors;
    }
}
