package com.skillbox.javapro21.api.response.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UsersStatResponse {
    @JsonProperty(value = "users_count")
    private Long usersCount;
    private Map<LocalDate, Long> dynamic;
    private YearsUsersStat yearsUsersStat;
}
