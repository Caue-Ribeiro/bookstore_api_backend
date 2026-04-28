package com.caue.bookstore.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bs_rate")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value;

    private String description;

    @ElementCollection
    private List<String> photosUrls = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Rate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photosUrls;
    }

    public void addPhotos(String photoUrl){

        photosUrls.add(photoUrl);
    }

    public List<String> getPhotosUrls() {
        return photosUrls;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }
}
