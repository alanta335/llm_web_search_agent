package com.smartagent.smartAgent.retriever;

import com.smartagent.smartAgent.service.impl.DataIngestionServiceImpl;
import com.smartagent.smartAgent.utility.CommonUtility;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Query;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.smartagent.smartAgent.utility.CommonUtility.MAX_TOKEN_SIZE;
import static com.smartagent.smartAgent.utility.CommonUtility.MAX_TOKEN_SIZE_FOR_EMBEDDING;

/**
 * Component for retrieving and preprocessing content for query resolution.
 * <p>
 * This class implements {@link ContentRetriever} and delegates to another retriever,
 * applying additional preprocessing steps to filter, extract, and refine retrieved content.
 */
@Slf4j
@Component
public class PreprocessingContentRetriever implements ContentRetriever {

    private static final int SENTENCE_FILTER_LENGTH = 10;
    private static final int GROUP_TEXT_LENGTH = 15;

    @Autowired
    private CommonUtility commonUtility;

    @Autowired
    private WebSearchContentRetriever webSearchContentRetriever;

    @Autowired
    private DataIngestionServiceImpl dataIngestionService;

    /**
     * Retrieves and preprocesses content based on the provided query.
     *
     * @param query the {@link Query} for which content needs to be retrieved and processed.
     * @return a list of {@link Content} objects that meet the query's requirements.
     */
    @Override
    public List<Content> retrieve(Query query) {
        try {
            List<Content> webContents = getWebContents(query);

            int totalTokenCount = commonUtility.calculateTokenCount(webContents);

            if (totalTokenCount > MAX_TOKEN_SIZE) {
                log.warn("Total token count exceeds the maximum limit. Consider reducing data.");
                // Uncomment below if token reduction logic is implemented
                // webContents = commonUtility.reduceTokenCount(query, webContents);
            }

            return webContents;
        } catch (Exception e) {
            log.error("Error while retrieving content: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Fetches web content and applies preprocessing steps.
     *
     * @param query the query for retrieving web content.
     * @return a list of preprocessed {@link Content} objects.
     */
    @NotNull
    private List<Content> getWebContents(@NotNull Query query) {
        try {
            List<Content> contents = webSearchContentRetriever.retrieve(query);

            return contents.stream()
                    .map(commonUtility::extractWebPageContentFromUrl)
                    .map(extractedContent -> processExtractedContent(query, extractedContent))
                    .filter(Objects::nonNull)
                    .filter(content -> StringUtils.isNotBlank(content.textSegment().text()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while fetching web contents: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Processes the extracted content to refine it based on token count and relevance.
     *
     * @param query            the query for which content is being processed.
     * @param extractedContent the content extracted from the web.
     * @return a processed {@link Content} object or null if processing fails.
     */
    private Content processExtractedContent(Query query, Content extractedContent) {
        try {
            int tokenCount = commonUtility.calculateTokenCount(extractedContent);
            if (tokenCount > MAX_TOKEN_SIZE_FOR_EMBEDDING) {
                String refactoredText = filterDataWithEmbedding(query, extractedContent);
                if (StringUtils.isBlank(refactoredText)) {
                    return null;
                }
                Content content = Content.from(new TextSegment(refactoredText, extractedContent.textSegment().metadata()));
                return commonUtility.filterRelevantData(query, content);
            }
            return commonUtility.filterRelevantData(query, extractedContent);
        } catch (Exception e) {
            log.error("Error processing extracted content: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Filters and refines data using embedding techniques.
     *
     * @param query            the query for which content is being filtered.
     * @param extractedContent the extracted content.
     * @return a refined string representation of the content.
     */
    @NotNull
    private String filterDataWithEmbedding(@NotNull Query query, Content extractedContent) {
        try {
            List<String> sentences = Arrays.asList(extractedContent.textSegment().text().split("(?<=[.!?])\\s*"));
            List<String> filteredSentences = sentences.stream()
                    .filter(sentence -> sentence.length() > SENTENCE_FILTER_LENGTH)
                    .toList();

            List<List<String>> groupedList = IntStream.range(0, (filteredSentences.size() + GROUP_TEXT_LENGTH - 1) / GROUP_TEXT_LENGTH)
                    .mapToObj(i -> filteredSentences.subList(i * GROUP_TEXT_LENGTH, Math.min((i + 1) * GROUP_TEXT_LENGTH, filteredSentences.size())))
                    .toList();

            groupedList.forEach(this::ingestDataToEmbeddingStore);

            List<Content> retrievedContent = retrieveDataFromEmbeddingStore(query.text());

            return String.join(" ", retrievedContent.stream().map(content -> content.textSegment().text()).toList());
        } catch (Exception e) {
            log.error("Error filtering data with embedding: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Ingests data into the embedding store for processing.
     *
     * @param content the content to be ingested.
     */
    private void ingestDataToEmbeddingStore(List<String> content) {
        try {
            String text = String.join(" ", content);
            Document document = new Document(text);
            dataIngestionService.ingestData(List.of(document));
        } catch (Exception e) {
            log.error("Error ingesting data to embedding store: {}", e.getMessage(), e);
        }
    }

    /**
     * Retrieves data from the embedding store based on a query.
     *
     * @param query the query string.
     * @return a list of {@link Content} objects retrieved from the embedding store.
     */
    private List<Content> retrieveDataFromEmbeddingStore(String query) {
        try {
            List<Content> contents = dataIngestionService.retrieveData(query);
            clearData();
            return contents;
        } catch (Exception e) {
            log.error("Error retrieving data from embedding store: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Clears temporary data in the embedding store.
     */
    private void clearData() {
        try {
            dataIngestionService.clearData();
        } catch (Exception e) {
            log.error("Error clearing data: {}", e.getMessage(), e);
        }
    }
}
