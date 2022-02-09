package com.skillbox.javapro21.api.response.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.api.response.View;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class AuthData implements Content {
    @JsonView({View.Posts.class, View.Profile.class, View.Friends.class, View.Auth.class})
    private long id;
    @JsonView({View.Posts.class, View.Profile.class, View.Friends.class})
    @JsonProperty("first_name")
    private String firstName;
    @JsonView({View.Posts.class, View.Profile.class, View.Friends.class})
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_time")
    private long regDate;
    @JsonView({View.Profile.class, View.Friends.class})
    @JsonProperty("birth_date")
    private long birthDate;
    private String email;
    @JsonView({View.Profile.class})
    private String phone;
    @JsonView({View.Posts.class, View.Profile.class, View.Friends.class})
    private String photo;
    @JsonView({View.Profile.class})
    private String about;
    @JsonView({View.Profile.class, View.Friends.class})
    private Map<String, String> city;
    @JsonView({View.Profile.class, View.Friends.class})
    private Map<String, String> country;
    @JsonProperty("message_permission")
    private MessagesPermission messagePermission;
    @JsonView({View.Dialogs.class, View.Profile.class})
    @JsonProperty("last_online_time")
    private long lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    @JsonView({View.Auth.class})
    private String token;
}