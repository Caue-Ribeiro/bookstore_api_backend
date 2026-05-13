package com.caue.bookstore.repositories;

import com.caue.bookstore.entities.Book;
import com.caue.bookstore.projections.BookProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    @Query(nativeQuery = true, value = """
                SELECT
                	bk.id AS book_id,
                	bk.title,
                	bk.isbn,
                	bk.release_date,
                	bk.stock,
                	bk.price,
                	bk.description,
                    bk.cover_image_url,
                	bc.type,
                    bc.id AS category_id,
                	bat.name AS author_name,
                    bat.last_name AS author_last_name,
                    bat.id AS author_id
                FROM
                	bs_book bk
                LEFT JOIN
                	bs_book_category bs_bkc ON bs_bkc.book_id = bk.id
                LEFT JOIN
                	bs_category bc ON bc.id = bs_bkc.category_id
                LEFT JOIN
                	bs_book_author bs_bkaut ON bs_bkaut.book_id = bk.id
                LEFT JOIN
                	bs_author bat ON bat.id = bs_bkaut.author_id
                WHERE\s
                	bk.id = :id;
            
            """)
    List<BookProjection> findBookById(UUID id);
}

