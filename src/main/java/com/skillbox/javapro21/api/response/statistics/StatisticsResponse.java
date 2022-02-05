package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StatisticsResponse {
    @JsonProperty(value = "users_count")
    private Long usersCount;
    @JsonProperty(value = "posts_count")
    private Long postsCount;
    @JsonProperty(value = "comments_count")
    private Long commentsCount;
    @JsonProperty(value = "likes_count")
    private Long likesCount;
}
