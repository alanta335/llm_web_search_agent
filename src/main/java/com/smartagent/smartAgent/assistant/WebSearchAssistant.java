package com.smartagent.smartAgent.assistant;


import dev.langchain4j.service.SystemMessage;

public interface WebSearchAssistant {
    @SystemMessage("You are a smart assistant, your job is to answer the question using your knowledge and the information provided." +
            "if any more information is need use the function to search the web to get necessary information needed to answer the question")
    String answer(String question);
}