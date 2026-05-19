package com.caue.bookstore.AIService;

import com.caue.bookstore.dto.BookRequestDTO;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService(tools = "webSearchTool")
public interface AssistantService {
    @SystemMessage({"""
            You are a specialized librarian AI data extractor.,
            Your task is to fetch realistic metadata for a book based on the provided title and map it to a specific structure.
            
            CRITICAL RULES:,
            1. IGNORE 'stock', 'price' and 'coverImageUrl': You must absolutely NOT generate, invent, or include values for the 'stock', 'price' or 'coverImageUrl' attributes. Leave them null or omitted entirely.,
            2. DATE FORMAT: The 'releaseDate' must be formatted strictly as an ISO-8601 date string (YYYY-MM-DD).,
            3. IDENTIFIERS: Since you do not know this system's internal database IDs, leave the 'categoriesIds' and 'authorsIds' arrays empty.,
            4. ISBN: The 'isbn' must have 13 digits and the datatype returned must Long.
            4. COMPLETENESS: Do your best to provide accurate data for the 'isbn', 'description'.
            """

    })
    BookRequestDTO extractBookData(@UserMessage String title);
}
