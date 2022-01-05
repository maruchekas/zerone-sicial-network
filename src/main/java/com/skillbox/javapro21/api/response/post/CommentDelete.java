package com.skillbox.javapro21.api.response.post;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentDelete implements Content {
    Long id;
}
