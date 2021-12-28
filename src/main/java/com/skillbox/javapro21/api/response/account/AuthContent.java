package com.skillbox.javapro21.api.response.account;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.Person;
import lombok.Data;

@Data
public class AuthContent implements Content {
    Person person;
}
