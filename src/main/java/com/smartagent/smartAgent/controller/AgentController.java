package com.smartagent.smartAgent.controller;

import com.smartagent.smartAgent.record.dto.response.WebSearchResponseDto;
import com.smartagent.smartAgent.service.WebSearchAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling web search queries using the WebSearchAgentService.
 * <p>
 * This controller exposes endpoints to interact with the WebSearchAgentService, allowing
 * users to send questions and receive responses with web search data.
 * </p>
 */
@RestController
public class AgentController {
    @Autowired
    private WebSearchAgentService webSearchAgentService;

    /**
     * Handles HTTP GET requests to the /web-search-agent endpoint.
     * <p>
     * This method takes a question as a query parameter, processes it using the
     * WebSearchAgentService, and returns a response containing web search results.
     * </p>
     *
     * @param question the user's question to process and search for.
     * @return a {@link ResponseEntity} containing a {@link WebSearchResponseDto}
     * with the search results or an error message.
     */
    @GetMapping("/web-search-agent")
    ResponseEntity<WebSearchResponseDto> webSearchAgent(@RequestParam String question) {
        try {
            WebSearchResponseDto response = new WebSearchResponseDto(webSearchAgentService.agentReplyWithWebSearchData(question));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new WebSearchResponseDto("Error during web searching. Please try again."));
        }
    }
}