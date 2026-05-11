package com.caue.bookstore.HTTPService;

import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TavilyConfig {

    @Value("${langchain4j.web-search-engine.tavily.api-key}")
    private String tavilyApiKey;


    @Bean
    public WebSearchEngine webSearchEngine(){

        WebSearchEngine engine =  TavilyWebSearchEngine.builder()
                .apiKey(tavilyApiKey)
                .build();

        return (request) -> {
            System.out.println("🚀 TAVILY SEARCH TRIGGERED: " + request.searchTerms());
            return engine.search(request);
        };
    }

    @Bean
    public WebSearchTool webSearchTool(WebSearchEngine webSearchEngine){
        return new WebSearchTool(webSearchEngine);
    }

}