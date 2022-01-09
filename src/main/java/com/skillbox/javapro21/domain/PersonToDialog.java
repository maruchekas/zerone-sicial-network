package com.skillbox.javapro21.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "person2dialog")
@Accessors(chain = true)
public class PersonToDialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private Person person;

    @OneToOne
    private Dialog dialog;

    @Column(name = "last_check")
    private LocalDateTime lastCheck;
}
