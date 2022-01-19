package com.skillbox.javapro21.api.response.like;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class LikeData implements Content {
    private String likes;
    private List<String> users;
}
