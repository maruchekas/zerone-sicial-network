package com.skillbox.javapro21.api.response.profile;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.Post;
import lombok.Data;

@Data
public class PostContent implements Content {
    Post post;
}
