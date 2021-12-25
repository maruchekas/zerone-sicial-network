package com.skillbox.javapro21.api.request.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRequest {
    String title;
    @JsonProperty("post_text")
    String postText;
}
