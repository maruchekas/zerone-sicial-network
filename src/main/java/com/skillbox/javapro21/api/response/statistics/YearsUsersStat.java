package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YearsUsersStat {
    @JsonProperty(value = "0-18")
    private Integer young;
    @JsonProperty(value = "18-25")
    private Integer teenager;
    @JsonProperty(value = "25-45")
    private Integer adult;
    @JsonProperty(value = "45+")
    private Integer elderly;
}
