package com.smartagent.smartAgent.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LLMConfiguration {

    @Value("${model-url}")
    private String llmUrl;

    @Value("${model-api-key}")
    private String llmApiKey;

    @Value("${model-name}")
    private String llmName;

    @Bean
    ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(llmUrl)
                .apiKey(llmApiKey)
                .modelName(llmName)
                .logRequests(true)
                .logResponses(true)
                .strictJsonSchema(true)
                .build();
    }

    @Bean
    OpenAiTokenizer tokenizer() {
        return new OpenAiTokenizer(OpenAiChatModelName.GPT_4_O);
    }
}
