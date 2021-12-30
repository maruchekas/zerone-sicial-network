package com.skillbox.javapro21.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ConfirmationUrl {
    private final String urlForRegisterComplete = "https://zerone-2022-develop.herokuapp.com/api/v1/account/register/complete";
    private final String urlForPasswordComplete = "https://zerone-2022-develop.herokuapp.com/api/v1/account/password/recovery/complete";
}
