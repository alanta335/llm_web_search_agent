package com.smartagent.smartAgent.retriever;

import com.smartagent.smartAgent.service.impl.DataIngestionServiceImpl;
import com.smartagent.smartAgent.utility.CommonUtility;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Query;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    @Autowired
    private CommonUtility commonUtility;

    @Autowired
    private WebSearchContentRetriever webSearchContentRetriever;

    @Autowired
    private DataIngestionServiceImpl dataIngestionService;

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
        List<Content> webContents = getWebContents(query);

        int totalTokenCount = commonUtility.calculateTokenCount(webContents);

        // If token count exceeds the limit, reduce the data
        if (totalTokenCount > MAX_TOKEN_SIZE) {
            webContents = commonUtility.reduceTokenCount(query, webContents);
        }

        return webContents;
    }

    @NotNull
    private List<Content> getWebContents(@NotNull Query query) {
        List<Content> contents = webSearchContentRetriever.retrieve(query);

        return contents.stream()
                .map(content -> commonUtility.extractWebPageContentFromUrl(content))
                .map(extractedContent -> {
                    Content filteredContent = commonUtility.filterRelevantData(query, List.of(extractedContent));
//                    if (Objects.nonNull(filteredContent)) {
//                        ingestDataToEmbeddingStore(filteredContent);
//                    }
                    return filteredContent;
                })
                .filter(Objects::nonNull)
                .filter(content -> StringUtils.isNotBlank(content.textSegment().text()))
                .collect(Collectors.toList());
    }

    private void ingestDataToEmbeddingStore(Content content) {
        Document document = new Document(content.textSegment().text(), content.textSegment().metadata());
        dataIngestionService.ingestData(List.of(document));
    }
}