package com.smartagent.smartAgent.retriever;

import com.smartagent.smartAgent.utility.CommonUtility;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Query;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.smartagent.smartAgent.utility.CommonUtility.MAX_TOKEN_SIZE;

/**
 * A component for retrieving and preprocessing content for query resolution.
 * <p>
 * This class implements {@link ContentRetriever} and acts as a delegate to another retriever,
 * applying additional preprocessing steps to filter, extract, and refine the retrieved content.
 * </p>
 */
@Slf4j
@Component
public class PreprocessingContentRetriever implements ContentRetriever {

    private final ContentRetriever delegate;

    @Autowired
    CommonUtility commonUtility;

    /**
     * Constructs a {@code PreprocessingContentRetriever} with a delegated {@link ContentRetriever}.
     * <p>
     * The provided {@link WebSearchContentRetriever} serves as the primary content retriever, and this
     * class enhances its functionality by preprocessing the retrieved data.
     * </p>
     *
     * @param webSearchContentRetriever the delegated {@link ContentRetriever} for retrieving web search results.
     */
    @Autowired
    public PreprocessingContentRetriever(WebSearchContentRetriever webSearchContentRetriever) {
        this.delegate = webSearchContentRetriever;
    }

    /**
     * Retrieves and preprocesses content based on the provided query.
     * <p>
     * This method applies several preprocessing steps to the content retrieved by the delegate, including:
     * <ul>
     *   <li>Extracting web page content from URLs.</li>
     *   <li>Filtering relevant data based on the query.</li>
     *   <li>Removing null or blank entries.</li>
     *   <li>Reducing content to fit within a predefined token limit, if necessary.</li>
     * </ul>
     * </p>
     *
     * @param query the {@link Query} for which content needs to be retrieved and processed.
     * @return a list of {@link Content} objects that meet the query's requirements.
     */
    @Override
    public List<Content> retrieve(Query query) {
        List<Content> contents = delegate.retrieve(query);
        List<Content> filteredContents = contents.stream()
                .map(content -> commonUtility.extractWebPageContentFromUrl(content))
                .map(extractedContent -> commonUtility.filterRelevantData(query, List.of(extractedContent)))
                .filter(Objects::nonNull)
                .filter(content -> StringUtils.isNotBlank(content.textSegment().text()))
                .collect(Collectors.toList());

        int totalTokenCount = commonUtility.calculateTokenCount(filteredContents);

        // If token count exceeds the limit, reduce the data
        if (totalTokenCount > MAX_TOKEN_SIZE) {
            filteredContents = commonUtility.reduceTokenCount(query, filteredContents);
        }

        return filteredContents;
    }
}