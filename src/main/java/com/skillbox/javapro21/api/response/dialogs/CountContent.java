package com.skillbox.javapro21.api.response.dialogs;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CountContent implements Content {
    private int count;
}
