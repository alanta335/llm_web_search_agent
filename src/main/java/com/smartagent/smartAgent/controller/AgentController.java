package com.smartagent.smartAgent.controller;

import com.smartagent.smartAgent.record.response.WebSearchResponseDto;
import com.smartagent.smartAgent.service.WebSearchAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AgentController {
    @Autowired
    WebSearchAgent webSearchAgent;

    @GetMapping("/web-search-agent")
    ResponseEntity<WebSearchResponseDto> webSearchAgent(@RequestParam String question) {
        WebSearchResponseDto response = new WebSearchResponseDto(webSearchAgent.agentReplyWithWebSearchData(question));
        return ResponseEntity.ok(response);
    }
}