package com.smartagent.smartAgent.config;

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
public class MasterAssistantConfiguration {
    @Autowired
    ChatLanguageModel chatLanguageModel;

    @Autowired
    PreprocessingContentRetriever preprocessingContentRetriever;

    /**
     * Configures and provides a bean for MasterAssistant.
     * <p>
     * This method creates a MasterAssistant instance, integrating various components such as the chat language model,
     * a query router for preprocessing content, and a retrieval augmenter. Additionally, it sets up a message window
     * chat memory to manage chat history with a limit of 20 messages.
     * </p>
     *
     * @return an instance of {@link MasterAssistant} configured with the required services and augmenters.
     */
    @Bean
    MasterAssistant createMasterAssistant() {

        // Let's create a query router that will route each query to both retrievers.
        QueryRouter queryRouter = new DefaultQueryRouter(preprocessingContentRetriever);

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        return AiServices.builder(MasterAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
