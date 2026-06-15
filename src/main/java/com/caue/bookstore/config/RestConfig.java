package com.caue.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

@Bean
    public RestClient restClient(){
    return RestClient.builder()
            .baseUrl("https://en.wikipedia.org/api/rest_v1/page/summary")
            .build();
}
}
