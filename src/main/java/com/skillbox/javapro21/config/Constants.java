package com.skillbox.javapro21.config;

public final class Constants {

    public static final String CLOUDINARY_AVATARS_FOLDER = "Zerone/Users/";
    public static final String BASE_ROBOTIC_AVA_URL = "https://robohash.org/";
    public static final String AVATAR_CONFIG = ".png?size=360x360&set=set";
    public static final int CAPTCHA_WIDTH = 193;
    public static final int CAPTCHA_HEIGHT = 57;
    public static final int CAPTCHA_LIFESPAN_IN_SEC = 3600;
    public static final String CAPTCHA_IMG_ENCODE_PREFIX = "data:captchaImage/png;base64, ";



    public enum FileType {
        IMAGE
    }


    private Constants() {
    }
}
