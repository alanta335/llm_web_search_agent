package com.smartagent.smartAgent.record.dto.response;

/**
 * A data transfer object (DTO) for representing responses from web search operations.
 * <p>
 * This record encapsulates a single field, {@code answer}, which contains the result
 * of a web search query or an error message.
 * </p>
 *
 * @param answer the response message from the web search operation.
 */
public record WebSearchResponseDto(String answer) {
}