package com.smartagent.smartAgent.service;

/**
 * Interface for a WebSearchAgent that provides functionality to retrieve answers
 * to questions through a web search or an integrated system (e.g., WebSearchAssistant).
 * <p>
 * Implementing classes are expected to define how the answer to a given question
 * is retrieved, which may involve querying a search engine, API, or other data sources.
 */
public interface WebSearchAgent {
    /**
     * Retrieves the answer to a given question.
     *
     * @param question The question for which an answer is requested.
     * @return The answer as a String.
     */
    String agentReplyWithWebSearchData(String question);

}