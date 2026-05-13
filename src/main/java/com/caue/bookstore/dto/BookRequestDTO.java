package com.caue.bookstore.dto;

import com.caue.bookstore.entities.Book;

import java.util.HashSet;
import java.util.Set;


public class BookRequestDTO extends BookDTO {

    private Set<Long> categoriesIds = new HashSet<>();

    private Set<Long> authorsIds = new HashSet<>();

    public BookRequestDTO() {
    }

    public BookRequestDTO(Book entity) {

        super(entity);

        entity.getAuthors().forEach(author -> authorsIds.add(author.getId()));

        entity.getCategories().forEach(category -> categoriesIds.add(category.getId()));
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
