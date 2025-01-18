package com.smartagent.smartAgent.service.impl;

import com.smartagent.smartAgent.assistant.WebSearchAssistant;
import com.smartagent.smartAgent.service.WebSearchAgent;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the WebSearchAgent interface that provides a method to fetch answers
 * from a primary assistant based on a given question.
 * <p>
 * This service uses the WebSearchAssistant to retrieve the answer for the provided query.
 * If an error occurs during the retrieval process, the exception is logged, and a
 * runtime exception is thrown with the appropriate error message.
 */
@Slf4j
@Service
public class WebSearchAgentImpl implements WebSearchAgent {

    @Autowired
    private WebSearchAssistant webSearchAssistant;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    /**
     * Fetches an answer to the provided question by querying the primary assistant.
     *
     * @param question The question for which an answer is to be retrieved.
     * @return The answer to the question provided by the primary assistant.
     * @throws RuntimeException If an error occurs while fetching the answer,
     *                          an exception will be logged and re-thrown.
     */
    @Override
    public String agentReplyWithWebSearchData(String question) {
        try {
            return webSearchAssistant.answer(question);
        } catch (Exception e) {
            log.error("Error in agentReplyWithWebSearchData: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    void testEmbedding() {

        TextSegment segment1 = TextSegment.from("I like football.");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("The weather is good today.");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        String userQuery = "What is your favourite sport?";
        Embedding queryEmbedding = embeddingModel.embed(userQuery).content();
        int maxResults = 1;
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .build();

        EmbeddingSearchResult<TextSegment> relevant = embeddingStore.search(request);
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.matches().getFirst();

        System.out.println("Question: " + userQuery); // What is your favourite sport?
        System.out.println("Response: " + embeddingMatch.embedded().text()); // I like football.
    }
}