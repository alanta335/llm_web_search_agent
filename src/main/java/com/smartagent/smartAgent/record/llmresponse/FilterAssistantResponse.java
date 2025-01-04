package com.smartagent.smartAgent.record.llmresponse;

import dev.langchain4j.model.output.structured.Description;

/**
 * A data transfer object (DTO) for representing responses from the FilterAssistant.
 * <p>
 * This record encapsulates a single field, {@code extractedData}, which contains the data
 * extracted by the assistant to answer a given question. If no relevant information is found,
 * the field will contain an empty string.
 * </p>
 *
 * @param extractedData the extracted data needed to answer the question, or an empty string if no information is found.
 */
public record FilterAssistantResponse(
        @Description("The extracted data needed to answer the question. If no information is found, return empty string.")
        String extractedData
) {
}
