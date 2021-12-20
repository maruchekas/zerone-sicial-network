package com.skillbox.javapro21.api.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;

}
