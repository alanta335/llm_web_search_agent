package com.smartagent.smartAgent.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DataIngestionServiceImpl {

    @Autowired
    private EmbeddingStoreIngestor ingestor;

    public void ingestData(List<Document> documents) {
        ingestor.ingest(documents);
    }
}