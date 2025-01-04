package com.smartagent.smartAgent.service.impl;

import com.smartagent.smartAgent.assistant.MasterAssistant;
import com.smartagent.smartAgent.service.WebSearchAgent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebSearchAgentImpl implements WebSearchAgent {

    @Autowired
    MasterAssistant masterAssistant;

    @Override
    public String agentReplyWithWebSearchData(String question) {
        return masterAssistant.answer(question);
    }

    @Override
    public String agentReply(String question) {
//        String answer = chatLanguageModel.generate(UserMessage.from(question)).content().text();
//        startConversationWith(masterAssistant);
        return "";
    }
}