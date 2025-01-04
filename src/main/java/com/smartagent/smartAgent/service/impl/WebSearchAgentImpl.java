package com.smartagent.smartAgent.service.impl;

import com.smartagent.smartAgent.assistant.MasterAssistant;
import com.smartagent.smartAgent.service.WebSearchAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the WebSearchAgent interface that provides a method to fetch answers
 * from a primary assistant based on a given question.
 * <p>
 * This service uses the MasterAssistant to retrieve the answer for the provided query.
 * If an error occurs during the retrieval process, the exception is logged, and a
 * runtime exception is thrown with the appropriate error message.
 */
@Slf4j
@Service
public class WebSearchAgentImpl implements WebSearchAgent {

    @Autowired
    MasterAssistant masterAssistant;

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
            return masterAssistant.answer(question);
        } catch (Exception e) {
            log.error("Error in agentReplyWithWebSearchData: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}