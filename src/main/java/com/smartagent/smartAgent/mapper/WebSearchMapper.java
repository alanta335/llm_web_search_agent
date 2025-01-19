package com.smartagent.smartAgent.mapper;

import com.smartagent.smartAgent.record.domain.WebSearchResult;
import com.smartagent.smartAgent.record.dto.response.WebSearchResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebSearchMapper {
    @Mapping(target = "answer", source = "result")
    WebSearchResponseDto mapWebSearchResultToWebSearchResponseDto(WebSearchResult result);
}
