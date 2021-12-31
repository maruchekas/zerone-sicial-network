package com.skillbox.javapro21.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.account.AuthData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CommentsData implements Content {
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("comment_text")
    private String commentText;
    private Long id;
    @JsonProperty("post_id")
    private Long postId;
    private LocalDateTime time;
    private AuthData author;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
