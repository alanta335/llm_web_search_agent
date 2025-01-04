package com.smartagent.smartAgent.record;

import dev.langchain4j.model.output.structured.Description;

public record FilterAssistantResponse(
        @Description("The extracted data needed to answer the question. If no information is found, return empty string.")
        String extractedData
) {
}
