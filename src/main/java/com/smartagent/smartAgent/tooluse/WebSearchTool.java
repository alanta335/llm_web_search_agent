package com.smartagent.smartAgent.tooluse;

import com.smartagent.smartAgent.retriever.PreprocessingContentRetriever;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WebSearchTool {

    @Autowired
    private PreprocessingContentRetriever preprocessingContentRetriever;

    @Tool("Function to search for extra information in web")
    List<String> webSearch(@P("search query") String webSearchQuery) {
        Query query = Query.from(webSearchQuery);
        List<Content> webContents = preprocessingContentRetriever.retrieve(query);
        return webContents.stream().map(Content::textSegment).map(TextSegment::text).toList();
    }
}