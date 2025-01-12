package com.smartagent.smartAgent.config;

import com.smartagent.smartAgent.assistant.FilterAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlaveAssistantConfiguration {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    /**
     * Configures and provides a bean for FilterAssistant.
     * <p>
     * This method creates a FilterAssistant instance using the provided chat language model.
     * The FilterAssistant can be used for specialized filtering tasks in the AI system.
     * </p>
     *
     * @return an instance of {@link FilterAssistant} configured with the required chat language model.
     */
    @Bean
    FilterAssistant createFilterAssistant() {
        return AiServices.builder(FilterAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
