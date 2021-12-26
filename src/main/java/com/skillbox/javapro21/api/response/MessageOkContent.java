package com.skillbox.javapro21.api.response;

import lombok.Data;

import java.util.Map;

@Data
public class MessageOkContent implements Content {
    String message;
}
