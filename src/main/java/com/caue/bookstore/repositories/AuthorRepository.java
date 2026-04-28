package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
