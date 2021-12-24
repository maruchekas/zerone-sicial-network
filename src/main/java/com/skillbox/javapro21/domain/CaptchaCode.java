package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.api.response.Content;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "captcha_codes")
public class CaptchaCode implements Content {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private Timestamp time;

    @Column(nullable = false)
    private String code;

    @Column(name = "secret_code", nullable = false)
    private String secretCode;
}