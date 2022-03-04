package com.skillbox.javapro21.config;

public final class Constants {
    public static final String CLOUDINARY_AVATARS_FOLDER = "Zerone/Users/";
    public static final String BASE_ROBOTIC_AVA_URL = "https://robohash.org/";
    public static final String RECOVERY_URL = "/api/v1/account/send_recovery_massage?email=";
    public static final String COMPLETE_REGISTER_URL = "/api/v1/account/register/complete?email=";
    public static final String WEATHER_ICON_URL = "http://openweathermap.org/img/wn/%s@2x.png";
    public static final String WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&lang=ru" +
            "&units=metric&appid=%s";
    public static final String AVATAR_CONFIG = ".png?size=360x360&set=set";
    public static final int CAPTCHA_WIDTH = 193;
    public static final int CAPTCHA_HEIGHT = 57;
    public static final int CAPTCHA_LIFESPAN_IN_SEC = 3600;
    public static final String CAPTCHA_IMG_ENCODE_PREFIX = "data:captchaImage/png;base64, ";
    public static final String CAPTCHA_CODE_ERR = "Неверный код с картинки";
    public static final String CONFIRMATION_CODE_ERR = "Не верный confirmation code";
    public static final String PASSWORD_CHANGE_SUCCESS = "Пароль успешно изменен";
    public static final String PASSWORD_CHANGE_ALLOW = "Пользователь может приступить к изменению пароля";
    public static final String MESSAGE_SENT_SUCCESS = "Ссылка отправлена на почту";
    public static final String UNAUTHORISED_USER_ERR = "Пользователь не авторизован";
    public static final String USER_NOT_FOUND_ERR = "Пользователя с данным айди не существует";
    public static final String USER_WASBLOCKED_ERR = "Пользователь заблокирован и не может смотреть посты";
    public static final String USER_EXISTS_ERR = "Пользователь с данным email уже подтвержден " +
            "или слишком много попыток пройти регистрацию по одному email";

    public enum FileType {
        IMAGE
    }

    private Constants() {
    }
}
