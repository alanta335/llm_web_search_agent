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

@Slf4j
@Component
public class PreprocessingContentRetriever implements ContentRetriever {

    private final ContentRetriever delegate;

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    public PreprocessingContentRetriever(WebSearchContentRetriever webSearchContentRetriever) {
        this.delegate = webSearchContentRetriever;
    }

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