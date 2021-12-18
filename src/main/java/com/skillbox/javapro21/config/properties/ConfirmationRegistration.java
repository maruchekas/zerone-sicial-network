package com.skillbox.javapro21.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "registration.verification")
public class ConfirmationRegistration {
    String url;
}
