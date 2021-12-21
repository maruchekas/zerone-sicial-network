package com.skillbox.javapro21.domain.enumeration;

public enum FriendshipStatusType {
    REQUEST("Запрос на добавление в друзья"),
    FRIEND("Друзья"),
    BLOCKED("Пользователь в в черном списке"),
    DECLINED("Запрос на добавление в друзья отклонен"),
    SUBSCRIBED("Подписан");

    private final String name;

    FriendshipStatusType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FriendshipStatusType{" +
                "name='" + name + '\'' +
                '}';
    }
}
