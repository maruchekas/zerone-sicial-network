package com.skillbox.javapro21.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * The NotificationTypeStatus enumeration.
 */
@Getter
@ToString
@AllArgsConstructor
public enum NotificationTypeStatus {
    POST("Новый пост"),
    POST_COMMENT("Комментарий к посту"),
    COMMENT_COMMENT("Ответ на комментарий"),
    FRIEND_REQUEST("Запрос дружбы"),
    MESSAGE("Личное сообщение");

    private final String name;
}
