package com.skillbox.javapro21.api.response.like;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class LikeBoolean implements Content {
    private Boolean likes;
}
