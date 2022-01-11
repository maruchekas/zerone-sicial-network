package com.skillbox.javapro21.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "type_id")
    private long typeId;
    @Column(name = "sent_time")
    private LocalDateTime sentTime;
    @Column(name = "entity_id")
    private long entityId;
    private String info;
}
