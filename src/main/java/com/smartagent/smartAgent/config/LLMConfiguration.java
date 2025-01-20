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

    @Value("${tool-model-name}")
    private String toolLLMName;

    /**
     * Configures and provides a bean for ChatLanguageModel.
     * <p>
     * This method creates an instance of OpenAiChatModel and it compatible LLM model using properties defined in the application configuration.
     * The model is configured to log both requests and responses, enforce strict JSON schema validation, and use the
     * specified base URL, API key, and model name.
     * </p>
     *
     * @return an instance of {@link ChatLanguageModel} configured for LLM integration.
     */
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
    ChatLanguageModel ToolLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(llmUrl)
                .apiKey(llmApiKey)
                .modelName(toolLLMName)
                .strictTools(true)
                .logRequests(true)
                .logResponses(true)
                .strictJsonSchema(true)
                .build();
    }

    /**
     * Configures and provides a bean for OpenAiTokenizer.
     * <p>
     * This method creates an OpenAiTokenizer using the GPT-4-O model name for tokenization purposes.
     * The tokenizer is essential for managing input and output token counts when interacting with the OpenAI API.
     * </p>
     *
     * @return an instance of {@link OpenAiTokenizer} configured for the GPT-4-O model which is also compatible with the other LLM model.
     */
    @Bean
    OpenAiTokenizer tokenizer() {
        return new OpenAiTokenizer(OpenAiChatModelName.GPT_4_O);
    }
}
