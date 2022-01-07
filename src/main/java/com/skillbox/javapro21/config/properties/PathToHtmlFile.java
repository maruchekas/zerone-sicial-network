package com.skillbox.javapro21.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class PathToHtmlFile {
    @Value(value = "${html.file.register}")
    private String register;
    @Value(value = "${html.file.recovery}")
    private String recovery;
}
