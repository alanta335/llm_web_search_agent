package com.smartagent.smartAgent.config;

import com.smartagent.smartAgent.assistant.DataFilterAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataFilterAssistantConfiguration {

    @Autowired
    @Qualifier("chatLanguageModel")
    private ChatLanguageModel chatLanguageModel;

    /**
     * Configures and provides a bean for DataFilterAssistant.
     * <p>
     * This method creates a DataFilterAssistant instance using the provided chat language model.
     * The DataFilterAssistant can be used for specialized filtering tasks in the AI system.
     * </p>
     *
     * @return an instance of {@link DataFilterAssistant} configured with the required chat language model.
     */
    @Bean
    DataFilterAssistant createDataFilterAssistant() {
        return AiServices.builder(DataFilterAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
