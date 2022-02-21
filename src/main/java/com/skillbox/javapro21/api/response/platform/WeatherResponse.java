package com.skillbox.javapro21.api.response.platform;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherResponse {
    private String city;
    private int temp;
    @JsonProperty("icon")
    private String forecastIcon;
    private String description;
}
