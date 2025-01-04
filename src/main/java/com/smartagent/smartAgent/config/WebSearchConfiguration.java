package com.smartagent.smartAgent.config;

import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.web.search.WebSearchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class WebSearchConfiguration {

    @Value("${search-engine-url}")
    private String searchEngineUrl;

    @Bean
    WebSearchContentRetriever getWebSearchContentRetriever() {
        // Here you can instantiate the WebSearchContentRetriever directly
        WebSearchEngine webSearchEngine = SearXNGWebSearchEngine.builder()
                .baseUrl(searchEngineUrl)
                .duration(Duration.ofSeconds(5))
                .logRequests(true)
                .logResponses(true)
                .build();

        return WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(10)
                .build();
    }
}
