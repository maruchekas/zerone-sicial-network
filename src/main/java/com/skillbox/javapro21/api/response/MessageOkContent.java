package com.skillbox.javapro21.api.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageOkContent implements Content {
    String message;
}
