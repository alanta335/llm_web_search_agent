package com.smartagent.smartAgent.assistant;


import dev.langchain4j.service.SystemMessage;

public interface MasterAssistant {
    @SystemMessage("You are a smart assistant, your job is to answer the question using your knowledge and the information provided.")
    String answer(String question);
}
