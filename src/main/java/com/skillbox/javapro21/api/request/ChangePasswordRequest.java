package com.skillbox.javapro21.api.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String token;
    private String password;
}
