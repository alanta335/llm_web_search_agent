package com.smartagent.smartAgent.utility;

import com.smartagent.smartAgent.assistant.DataFilterAssistant;
import com.smartagent.smartAgent.record.llmresponse.DataFilterAssistantResponse;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing common methods for filtering, processing, and managing
 * content data, ensuring that token counts are within the allowed limit and extracting
 * relevant data from content sources such as URLs.
 */
@Slf4j
@Component
public class CommonUtility {

    public static final int MAX_TOKEN_SIZE = 8_000;
    public static final int MAX_TOKEN_SIZE_FOR_EMBEDDING = 1_000;
    @Autowired
    private DataFilterAssistant dataFilterAssistant;
    @Autowired
    private OpenAiTokenizer tokenizer;

    /**
     * Filters relevant data from the given contents based on the provided query.
     *
     * @param query   The query to be used for filtering.
     * @param content The list of content to filter.
     * @return A {@link Content} object containing the relevant extracted data, or null if no relevant data is found.
     */
    public Content filterRelevantData(Query query, Content content) {
        String question = query.text();
        String data = content.textSegment().text();

        if (StringUtils.isNotBlank(data)) {
            try {
                DataFilterAssistantResponse dataFilterAssistantResponse = dataFilterAssistant.answer(question, data);
                if (StringUtils.isNotBlank(dataFilterAssistantResponse.extractedData())) {
                    return Content.from(new TextSegment(dataFilterAssistantResponse.extractedData(), content.textSegment().metadata()));
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Extracts additional content from a webpage by connecting to the URL specified in the content.
     *
     * @param content The content object containing the URL to extract data from.
     * @return A new {@link Content} object with the processed text, combining the original content and the web page's text.
     */
    public Content extractWebPageContentFromUrl(Content content) {
        String processedText = content.textSegment().text();
        String url = content.textSegment().metadata().getString("url");
        String webData = "";

        try {
            Document doc = Jsoup.connect(url).get();
            String webPageText = doc.text();
            if (StringUtils.isNotBlank(webPageText)) {
                webData = processedText + "\n" + webPageText;
            }
        } catch (Exception e) {
            log.error("Error fetching content from URL: {}", e.getMessage());
        }

        return Content.from(new TextSegment(webData, content.textSegment().metadata()));
    }

    /**
     * Calculates the total token count of the given content list by estimating the token count
     * of each content's text segment.
     *
     * @param contents The list of content objects whose token counts are to be calculated.
     * @return The total token count across all content items.
     */
    public int calculateTokenCount(List<Content> contents) {
        return contents.stream()
                .mapToInt(content -> tokenizer.estimateTokenCountInText(content.textSegment().text()))
                .sum();
    }

    public int calculateTokenCount(Content content) {
        return tokenizer.estimateTokenCountInText(content.textSegment().text());
    }

    /**
     * Splits a content object into smaller parts if its token count exceeds the maximum allowed size.
     * Each part will contain a portion of the original content, ensuring each part stays under the token limit.
     *
     * @param content The content to be split into smaller parts.
     * @return A list of smaller content parts.
     */
    private List<Content> splitContentIntoSmallerParts(Content content) {
        // This method will split a content object into smaller parts where each part has a token count <= MAX_TOKEN_SIZE
        List<Content> smallerParts = new ArrayList<>();
        String contentText = content.textSegment().text();
        int tokenCount = 0;
        StringBuilder currentTextPart = new StringBuilder();

        for (String word : contentText.split("\\s+")) {
            int wordTokenCount = tokenizer.estimateTokenCountInText(word);

            if (tokenCount + wordTokenCount <= MAX_TOKEN_SIZE) {
                currentTextPart.append(word).append(" ");
                tokenCount += wordTokenCount;
            } else {
                // If adding the word exceeds the limit, save the current part and start a new one
                smallerParts.add(new Content(new TextSegment(currentTextPart.toString().trim(), content.textSegment().metadata())));
                currentTextPart.setLength(0); // Reset StringBuilder for the next part
                currentTextPart.append(word).append(" ");
                tokenCount = wordTokenCount;
            }
        }

        // Add the last part if it exists
        if (!currentTextPart.isEmpty()) {
            smallerParts.add(new Content(new TextSegment(currentTextPart.toString().trim(), content.textSegment().metadata())));
        }

        return smallerParts;
    }

    /**
     * Creates optimal batches of content, ensuring that no batch exceeds the maximum token size.
     *
     * @param contents The list of content to be grouped into batches.
     * @return A list of batches, where each batch contains content under the token limit.
     */
    private List<List<Content>> createOptimalBatchesUnderTokenLimit(List<Content> contents) {
        List<List<Content>> batches = new ArrayList<>();
        List<Content> currentBatch = new ArrayList<>();
        int currentTokenCount = 0;

        for (Content content : contents) {
            int contentTokenCount = tokenizer.estimateTokenCountInText(content.textSegment().text());

            // Check if the current content can be added to the current batch
            if (currentTokenCount + contentTokenCount <= MAX_TOKEN_SIZE) {
                currentBatch.add(content);
                currentTokenCount += contentTokenCount;
            } else {
                // If it exceeds the limit, finalize the current batch and start a new one
                if (!currentBatch.isEmpty()) {
                    batches.add(currentBatch);  // Add the current batch to the list
                }
                // Start a new batch with the current content
                currentBatch = new ArrayList<>();
                currentBatch.add(content);
                currentTokenCount = contentTokenCount;  // Reset token count for the new batch
            }
        }

        // Add the last batch if it has content
        if (!currentBatch.isEmpty()) {
            batches.add(currentBatch);
        }

        return batches;
    }
}
