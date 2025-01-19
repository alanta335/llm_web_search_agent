package com.smartagent.smartAgent.service;

import com.smartagent.smartAgent.record.domain.WebSearchResult;

/**
 * Interface for a WebSearchAgentService that provides functionality to retrieve answers
 * to questions through a web search or an integrated system (e.g., WebSearchAssistant).
 * <p>
 * Implementing classes are expected to define how the answer to a given question
 * is retrieved, which may involve querying a search engine, API, or other data sources.
 */
public interface WebSearchAgentService {
    /**
     * Retrieves the answer to a given question.
     *
     * @param question The question for which an answer is requested.
     * @return The answer as a String.
     */
    WebSearchResult agentReplyWithWebSearchData(String question);

}