package com.smartagent.smartAgent.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EmbeddingStoreContentRetrieverConfig {

    private final EmbeddingStore<TextSegment> embeddingStore;

    private final EmbeddingModel embeddingModel;

    @Bean
    public EmbeddingStoreContentRetriever getEmbeddingStoreContentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.85)
                .build();
    }
}
