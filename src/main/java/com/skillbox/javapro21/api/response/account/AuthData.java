package com.skillbox.javapro21.api.response.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class AuthData implements Content {
    private long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_time")
    private long regDate;
    @JsonProperty("birth_date")
    private long birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private Map<String, String> city;
    private Map<String, String> country;
    @JsonProperty("message_permission")
    private MessagesPermission messagePermission;
    @JsonProperty("last_online_time")
    private long lastOnlineTime;
    @JsonProperty("is_blocked")
    private String isBlocked;
    private String token;
}

