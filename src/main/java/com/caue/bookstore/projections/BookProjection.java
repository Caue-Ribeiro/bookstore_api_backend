package com.caue.bookstore.projections;

import com.caue.bookstore.enums.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface BookProjection {

    UUID getBookId();
    String getTitle();
    String getIsbn();
    LocalDate getReleaseDate();
    Integer getStock();
    BigDecimal getPrice();
    String getDescription();
    Long getCategoryId();
    CategoryType getType();
    Long getAuthorId();
    String getAuthorName();
    String getAuthorLastName();

}
