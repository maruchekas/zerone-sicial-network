package com.skillbox.javapro21.api.response.account;

import com.skillbox.javapro21.api.response.Data;

import java.util.Map;

@lombok.Data
public class AccountData implements Data {
    Map<String, String> data;
}
