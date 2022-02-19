package com.skillbox.javapro21.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private Timestamp time;

    @Column(nullable = false)
    private String code;

    @Column(name = "secret_code", nullable = false)
    private String secretCode;
}