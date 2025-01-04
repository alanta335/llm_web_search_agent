package com.smartagent.smartAgent.config;

import com.smartagent.smartAgent.assistant.FilterAssistant;
import com.smartagent.smartAgent.assistant.MasterAssistant;
import com.smartagent.smartAgent.retriever.PreprocessingContentRetriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlaveAssistantConfiguration {

    @Autowired
    ChatLanguageModel chatLanguageModel;

    @Bean
    FilterAssistant createFilterAssistant() {
        return AiServices.builder(FilterAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
