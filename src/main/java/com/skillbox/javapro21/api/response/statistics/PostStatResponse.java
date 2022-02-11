package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.YearMonth;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PostStatResponse {
    @JsonProperty(value = "posts_count")
    private Long countPosts;
    private Map<YearMonth, Long> posts;
    @JsonProperty(value = "posts_by_hour")
    private Map<Integer, Long> postsByHour;
}
