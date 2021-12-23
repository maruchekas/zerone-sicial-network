package com.skillbox.javapro21.api.response.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
public class AuthContent implements Content {
    private long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_time")
    private Timestamp regDate;
    @JsonProperty("birth_date")
    private Timestamp birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private Map<String, String> city;
    private Map<String, String> country;
    @JsonProperty("message_permission")
    private MessagesPermission messagePermission;
    @JsonProperty("last_online_time")
    private Timestamp lastOnlineTime;
    @JsonProperty("is_blocked")
    private String isBlocked;
    private String token;

    public AuthContent(Person person, String token) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.regDate = Timestamp.valueOf(person.getRegDate());
        this.birthDate = Timestamp.valueOf(person.getBirthDate());
        this.email = person.getEmail();
        this.phone = person.getPhone();
        this.photo = person.getPhoto();
        this.about = person.getAbout();
        this.city = new HashMap<String, String>() {{
            put("id", person.getId().toString());
            put("City", person.getTown());
        }};
        this.country = new HashMap<String, String>() {{
            put("id", person.getId().toString());
            put("Country", person.getCountry());
        }};
        this.messagePermission = person.getMessagesPermission();
        this.lastOnlineTime = Timestamp.valueOf(person.getLastOnlineTime());
        this.isBlocked = isBlockedPerson(person);
        this.token = token;
    }

    private String isBlockedPerson(Person person){
        return person.getIsBlocked() == 0 ? "false" : "true";
    }
}

//{
//        "error": "string",
//        "timestamp": 1559751301818,
//        "data": {
//        "id": 1,
//        "first_name": "Петр",
//        "last_name": "Петрович",
//        "reg_date": 1559751301818,
//        "birth_date": 1559751301818,
//        "email": "petr@mail.ru",
//        "phone": "89100000000",
//        "photo": "https://...../photos/image123.jpg",
//        "about": "Родился в небольшой, но честной семье",
//        "city": {
//        "id": 1,
//        "title": "Москва"
//        },
//        "country": {
//        "id": 1,
//        "title": "Россия"
//        },
//        "messages_permission": "ALL",
//        "last_online_time": 1559751301818,
//        "is_blocked": false,
//        "token": "1q2e3e3r4t5"
//        }
//        }
