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

    /**
     * Configures and provides a bean for WebSearchContentRetriever.
     * <p>
     * This method creates a WebSearchContentRetriever instance by setting up a WebSearchEngine with the specified
     * search engine URL. It configures the engine with a timeout duration of 5 seconds and enables logging for
     * requests and responses. The retriever is further configured to return a maximum of 10 results.
     * </p>
     *
     * @return an instance of {@link WebSearchContentRetriever} configured for web search integration.
     */
    @Bean
    WebSearchContentRetriever getWebSearchContentRetriever() {
        WebSearchEngine webSearchEngine = SearXNGWebSearchEngine.builder()
                .baseUrl(searchEngineUrl)
                .duration(Duration.ofSeconds(5))
                .logRequests(true)
                .logResponses(true)
                .build();

        return WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                //TODO: need to decided if it need to be configurable or hardcoded and value need to be changed
                .maxResults(3)
                .build();
    }
}
