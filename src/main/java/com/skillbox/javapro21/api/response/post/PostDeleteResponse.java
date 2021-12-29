package com.skillbox.javapro21.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDeleteResponse implements Content {
    Long id;
}
