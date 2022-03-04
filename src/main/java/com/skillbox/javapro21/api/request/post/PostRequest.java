package com.skillbox.javapro21.api.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostRequest {
    private String title;
    @JsonProperty("post_text")
    private String postText;
    private String[] tags;
}
