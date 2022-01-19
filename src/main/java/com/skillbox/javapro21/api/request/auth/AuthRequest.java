package com.skillbox.javapro21.api.request.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
