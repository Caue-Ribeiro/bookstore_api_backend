package com.caue.bookstore.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "bs_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(unique = true)
    private String isbn;

    private LocalDate releaseDate;

    private Integer stock;

    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "book")
    private List<Rate> rates = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "bs_book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns =
    @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "bs_book_category", joinColumns = @JoinColumn(name = "book_id"),inverseJoinColumns =
    @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Book() {
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

    public void addAuthor(Author author) {
        if (author == null) {
            return;
        }
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        if (author == null) {
            return;
        }
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void addCategory(Category category) {
        if (category == null) {
            return;
        }
        categories.add(category);
        category.getBooks().add(this);
    }

    public void removeCategory(Category category) {
        if (category == null) {
            return;
        }
        categories.remove(category);
        category.getBooks().remove(this);
    }


    public List<Rate> getRates() {
        return rates;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public Set<Author> getAuthors() {
        return authors;
    }
}
