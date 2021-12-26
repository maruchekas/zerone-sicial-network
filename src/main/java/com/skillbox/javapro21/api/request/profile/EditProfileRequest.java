package com.skillbox.javapro21.api.request.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EditProfileRequest implements Content {
    private Long id;
    @JsonProperty(value = "first_name")
    private String firstName;
    @JsonProperty(value = "last_name")
    private String lastName;
    @JsonProperty(value = "birth_date")
    private LocalDateTime birthDate;
    @JsonProperty(value = "reg_date")
    private LocalDateTime regDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String town;
    private String country;
}
