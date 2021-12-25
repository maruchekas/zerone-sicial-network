package com.skillbox.javapro21.api.response.profile;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.Person;
import lombok.Data;

@Data
public class PersonContent implements Content {
    Person person;
}
