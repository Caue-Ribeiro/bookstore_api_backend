package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.Author;
import com.caue.bookstore.projections.AuthorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author,Long> {


    @Query(nativeQuery = true, value = """

    SELECT
    	aut.id AS author_id,\s
    	aut.name,
    	aut.last_name,
    	bk.id AS book_id,
    	bk.title,
     	bk.isbn,
     	bk.release_date,
     	bk.stock,
     	bk.price,
     	bk.description,
     	bk.cover_image_url
    FROM
    	bs_author aut
    JOIN
    	bs_book_author baut ON baut.author_id = aut.id
    JOIN
    	bs_book bk ON bk.id = baut.book_id
    WHERE
    	aut.id = :id
""")
    List<AuthorProjection>findAuthorById(Long id);
}
