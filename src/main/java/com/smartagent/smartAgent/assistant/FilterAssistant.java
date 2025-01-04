package com.smartagent.smartAgent.assistant;


import com.smartagent.smartAgent.record.llmresponse.FilterAssistantResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface FilterAssistant {
    @SystemMessage("Your a smart assistant, your job is to extract relevant information that is needed to answer the question.")
    @UserMessage("""
            The question is: {{question}}
            Extract relevant information from the following data which is needed to give answer to question: {{data}}""")
    FilterAssistantResponse answer(@V("question") String question, @V("data") List<String> data);
}
