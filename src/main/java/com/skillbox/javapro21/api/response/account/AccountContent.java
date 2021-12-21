package com.skillbox.javapro21.api.response.account;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;

import java.util.Map;

@Data
public class AccountContent implements Content {
    Map<String, String> data;
}
