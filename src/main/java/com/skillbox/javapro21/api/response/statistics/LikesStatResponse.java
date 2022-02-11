package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.YearMonth;
import java.util.Map;

@Data
@Accessors(chain = true)
public class LikesStatResponse {
    @JsonProperty(value = "likes_count")
    private Long likesCount;
    private Map<YearMonth, Long> likes;
    @JsonProperty(value = "likes_by_hour")
    private Map<Integer, Long> likesByHour;
}
