package com.smartagent.smartAgent.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.mistralai.MistralAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EmbeddingConfiguration {

    @Value("${embedding-model-name}")
    private String modelName;

    @Value("${embedding-api-key}")
    private String apiKey;

    @Bean
    EmbeddingModel getEmbeddingModel() {
        return MistralAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .logRequests(true)
                .logResponses(true)
                .modelName(modelName)
                .build();
    }

    @Bean
    EmbeddingStore<TextSegment> getEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}
