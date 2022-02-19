package com.skillbox.javapro21.api.response.platform;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanguageData implements Content {

    private Long id;
    @JsonProperty("type_id")
    private Long typeId;
    @JsonProperty("sent_time")
    private Long sentTime;
    @JsonProperty("entity_id")
    private Long entityId;
    private String info;

}
