package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.Author;
import com.caue.bookstore.projections.BookProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author,Long> {


    @Query(nativeQuery = true, value = """

    SELECT
    	aut.id AS author_id,\s
    	aut.name AS author_name,
    	aut.last_name AS author_last_name,
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
    List<BookProjection>findAuthorById(Long id);

    @Override
    @Modifying
    @Query(nativeQuery = true, value = """

WITH deleted_books AS (
DELETE FROM bs_book_author\s
WHERE author_id = :id
)
DELETE FROM bs_author
WHERE id = :id

""")
    void deleteById(Long id);

    @Query("""
    SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%',:query,'%')) OR 
    LOWER(a.lastName) LIKE LOWER(CONCAT('%',:query,'%'))  
""")
    Page<Author> searchAuthor(@Param("query") String query, Pageable pageable);
}
