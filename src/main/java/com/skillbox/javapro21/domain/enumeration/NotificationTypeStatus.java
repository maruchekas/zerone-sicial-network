package com.skillbox.javapro21.domain.enumeration;

/**
 * The NotificationTypeStatus enumeration.
 */
public enum NotificationTypeStatus {
    POST("Новый пост"),
    POST_COMMENT("Комментарий к посту"),
    COMMENT_COMMENT("Ответ на комментарий"),
    FRIEND_REQUEST("Запрос дружбы"),
    MESSAGE("Личное сообщение");

    private final String name;

    NotificationTypeStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NotificationTypeStatus{" +
                "name='" + name + '\'' +
                '}';
    }
}
