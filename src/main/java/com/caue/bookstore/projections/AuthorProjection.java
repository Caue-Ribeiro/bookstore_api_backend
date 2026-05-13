package com.caue.bookstore.projections;

import java.util.UUID;

public interface AuthorProjection extends BookProjection{

    Long getAuthorId();
    String getName();
    String getLastName();

}
