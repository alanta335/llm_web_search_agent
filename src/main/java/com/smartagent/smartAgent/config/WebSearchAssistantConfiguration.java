package com.smartagent.smartAgent.config;

import com.smartagent.smartAgent.assistant.WebSearchAssistant;
import com.smartagent.smartAgent.retriever.PreprocessingContentRetriever;
import com.smartagent.smartAgent.tooluse.WebSearchTool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSearchAssistantConfiguration {
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    @Qualifier("chatLanguageModel")
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    @Qualifier("ToolLanguageModel")
    private ChatLanguageModel toolLanguageModel;

    @Autowired
    private PreprocessingContentRetriever preprocessingContentRetriever;

    @Autowired
    private WebSearchTool webSearchTool;

    /**
     * Configures and provides a bean for WebSearchAssistant.
     * <p>
     * This method creates a WebSearchAssistant instance, integrating various components such as the chat language model,
     * a query router for preprocessing content, and a retrieval augmenter. Additionally, it sets up a message window
     * chat memory to manage chat history with a limit of 20 messages.
     * </p>
     *
     * @return an instance of {@link WebSearchAssistant} configured with the required services and augmenters.
     */
    @Bean
    WebSearchAssistant createWebSearchAssistant() {

        //TODO: Implement query transformer to refine the user query (question) before it is sent to the LLM
//        QueryTransformer queryTransformer = new DefaultQueryTransformer();

        // this is the retriever that will be used to retrieve the content for the LLM
        // Additional ContentRetriever can be added to the list to retrieve content from other sources
        //TODO: Add other ContentRetriever to retrieve content from other sources
//        QueryRouter queryRouter = new DefaultQueryRouter(preprocessingContentRetriever, embeddingStoreContentRetriever);
        QueryRouter queryRouter = new DefaultQueryRouter(preprocessingContentRetriever);

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

        return AiServices.builder(WebSearchAssistant.class)
                .chatLanguageModel(toolLanguageModel)
//                .retrievalAugmentor(retrievalAugmentor)
                .tools(webSearchTool)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
