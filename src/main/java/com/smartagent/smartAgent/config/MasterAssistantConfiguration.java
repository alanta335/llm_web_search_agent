package com.smartagent.smartAgent.config;

import com.smartagent.smartAgent.assistant.MasterAssistant;
import com.smartagent.smartAgent.retriever.PreprocessingContentRetriever;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MasterAssistantConfiguration {
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private PreprocessingContentRetriever preprocessingContentRetriever;

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

        ContentRetriever embeddingStoreContentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.85)
                .build();

        //TODO: Implement query transformer to refine the user query (question) before it is sent to the LLM
//        QueryTransformer queryTransformer = new DefaultQueryTransformer();

        // this is the retriever that will be used to retrieve the content for the LLM
        // Additional ContentRetriever can be added to the list to retrieve content from other sources
        //TODO: Add other ContentRetriever to retrieve content from other sources
        QueryRouter queryRouter = new DefaultQueryRouter(preprocessingContentRetriever, embeddingStoreContentRetriever);

        //TODO: Implement content aggregator to aggregate the retrieved contents into a single list
//        ContentAggregator contentAggregator = new DefaultContentAggregator();

        //TODO: Implement content injector to inject the content into the LLM
//        ContentInjector contentInjector = new DefaultContentInjector();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
//                .queryTransformer(queryTransformer)
                .queryRouter(queryRouter)
//                .contentAggregator(contentAggregator)
//                .contentInjector()
                .build();

        return AiServices.builder(MasterAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
