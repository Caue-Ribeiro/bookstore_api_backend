package com.caue.bookstore.entities;

import java.util.List;

public record ReaderProfileResponse(
    ReaderArchetype readerArchetype,
    List<Recommendation> recommendations
) {
    public record ReaderArchetype(
        String title,
        String description
    ) {}

    public record Recommendation(
        String title,
        String author,
        String publishedYear,
        String matchReason
    ) {}
}