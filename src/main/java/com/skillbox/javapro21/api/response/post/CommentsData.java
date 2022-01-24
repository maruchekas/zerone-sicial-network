package com.skillbox.javapro21.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.account.AuthData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

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
    private AuthData author;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private boolean thisPersonDidLike;
    private int likes;
    @JsonProperty("sub_comments")
    private List<CommentsData> subComments;
}
