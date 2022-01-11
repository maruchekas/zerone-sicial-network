package com.skillbox.javapro21.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

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
    private long time;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
