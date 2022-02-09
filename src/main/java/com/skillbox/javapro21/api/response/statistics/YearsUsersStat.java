package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YearsUsersStat {
    @JsonProperty(value = "0-18")
    private String young;
    @JsonProperty(value = "18-25")
    private String teenager;
    @JsonProperty(value = "25-45")
    private String adult;
    @JsonProperty(value = "45+")
    private String elderly;
}
