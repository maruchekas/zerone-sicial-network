package com.skillbox.javapro21.api.request.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;

@Data
public class EditProfileRequest implements Content {
    @JsonProperty(value = "first_name")
    private String firstName;
    @JsonProperty(value = "last_name")
    private String lastName;
    @JsonProperty(value = "birth_date")
    private long birthDate;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
}
