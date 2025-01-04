package com.smartagent.smartAgent.utility;

import com.smartagent.smartAgent.assistant.FilterAssistant;
import com.smartagent.smartAgent.record.FilterAssistantResponse;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CommonUtility {
    @Autowired
    FilterAssistant filterAssistant;
    @Autowired
    OpenAiTokenizer tokenizer;

    public static final int MAX_TOKEN_SIZE = 8_000;

    public Content filterRelevantData(Query query, List<Content> contents) {
        Metadata metadata = new Metadata();

        String urls = contents.stream()
                .map(content -> content.textSegment().metadata().getString("url"))
                .filter(StringUtils::isNotBlank) // Ensure no null or blank URLs
                .collect(Collectors.joining(","));

        metadata.put("url", urls);
        String question = query.text();
        List<String> data = contents.stream()
                .filter(content -> StringUtils.isNotBlank(content.textSegment().text()))
                .map(content -> content.textSegment().text()).toList();

        if (CollectionUtils.isNotEmpty(data)) {
            try {
                FilterAssistantResponse filterAssistantResponse = filterAssistant.answer(question, data);
                if (StringUtils.isNotBlank(filterAssistantResponse.extractedData())) {
                    return Content.from(new TextSegment(filterAssistantResponse.extractedData(), metadata));
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

    public Content extractWebPageContentFromUrl(Content content) {
        String processedText = content.textSegment().text();
        String url = content.textSegment().metadata().getString("url");

        try {
            Document doc = Jsoup.connect(url).get();
            String webPageText = doc.text();
            if (StringUtils.isNotBlank(webPageText)) {
                processedText = processedText + "\n" + webPageText;
            }
        } catch (Exception e) {
            log.error("Error fetching content from URL: {}", e.getMessage());
        }

        return Content.from(new TextSegment(processedText, content.textSegment().metadata()));
    }

    public int calculateTokenCount(List<Content> contents) {
        return contents.stream()
                .mapToInt(content -> tokenizer.estimateTokenCountInText(content.textSegment().text()))
                .sum();
    }

    public List<Content> reduceTokenCount(Query query, List<Content> contents) {
        // Step 1: Split large contents into smaller parts if needed
        List<Content> processedContents = contents.stream()
                .flatMap(content -> {
                    int tokenCount = calculateTokenCount(List.of(content));
                    if (tokenCount > MAX_TOKEN_SIZE) {
                        // Split the content into smaller parts (each less than MAX_TOKEN_SIZE)
                        return splitContentIntoSmallerParts(content).stream();
                    } else {
                        // If the content is already within the limit, return it as is
                        return Stream.of(content);
                    }
                })
                .collect(Collectors.toList());

        // Step 2: Create optimal batches where no batch exceeds the token limit
        List<List<Content>> batches = createOptimalBatchesUnderTokenLimit(processedContents);

        // Step 3: Filter relevant data for each batch

        return batches.stream()
                .map(batch -> filterRelevantData(query, batch))
                .filter(Objects::nonNull)
                .filter(content -> StringUtils.isNotBlank(content.textSegment().text()))
                .collect(Collectors.toList());
    }

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
