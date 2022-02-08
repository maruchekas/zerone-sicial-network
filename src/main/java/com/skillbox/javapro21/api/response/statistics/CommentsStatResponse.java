package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.YearMonth;
import java.util.Map;

@Data
@Accessors(chain = true)
public class CommentsStatResponse {
    @JsonProperty(value = "comments_count")
    private Long commentsCount;
    private Map<YearMonth, Long> comments;
    @JsonProperty(value = "comments_by_hour")
    private Map<Integer, Long> commentsByHour;
}
