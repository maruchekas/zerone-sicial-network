package com.skillbox.javapro21.api.request.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeRequest {
    @JsonProperty("item_id")
    private Long itemId;
    private String type;
}
