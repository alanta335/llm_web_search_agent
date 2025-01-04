package com.smartagent.smartAgent.assistant;


import dev.langchain4j.service.SystemMessage;

public interface MasterAssistant {
    String answer(String question);
}
