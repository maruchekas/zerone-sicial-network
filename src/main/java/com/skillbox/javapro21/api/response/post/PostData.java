package com.skillbox.javapro21.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.account.AuthData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostData implements Content {
    private Long id;
    private long time;
    private AuthData author;
    private String title;
    @JsonProperty("post_text")
    private String postText;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private boolean my_like;
    private int likes;
    private List<CommentsData> comments;
    private String type;
    private List<String> tags;
}
