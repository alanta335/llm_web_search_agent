package com.smartagent.smartAgent.assistant;


import com.smartagent.smartAgent.record.llmresponse.DataFilterAssistantResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface DataFilterAssistant {
    @SystemMessage("Your a smart assistant, your job is to extract relevant information that is needed to answer the question it should be short and precise.")
    @UserMessage("""
            The question is: {{question}}
            Extract relevant information from the following data which is needed to give answer to question: {{data}}""")
    DataFilterAssistantResponse answer(@V("question") String question, @V("data") List<String> data);
}