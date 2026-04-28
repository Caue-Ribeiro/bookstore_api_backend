package com.caue.bookstore.entities;

import com.caue.bookstore.enums.CategoryType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bs_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoryType type;


    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new HashSet<>();


    public Category() {
    }

    public Category(Long id, CategoryType type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Set<Book> getBooks() {
        return books;
    }
}
