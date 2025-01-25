package com.smartagent.smartAgent.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataIngestionServiceImpl {

    private final EmbeddingStore<TextSegment> embeddingStore;

    private final EmbeddingStoreIngestor ingestor;

    private final EmbeddingStoreContentRetriever embeddingStoreContentRetriever;

    public void ingestData(List<Document> documents) {
        ingestor.ingest(documents);
    }

    public List<Content> retrieveData(String question) {
        List<Content> contents = embeddingStoreContentRetriever.retrieve(Query.from(question));
        log.debug("Retrieved data: {}", contents);
        return contents;
    }

    public void clearData() {
        embeddingStore.removeAll();
    }
}