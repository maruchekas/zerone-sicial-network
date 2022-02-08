package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import com.skillbox.javapro21.domain.marker.HavePerson;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "notif_types")
@Accessors(chain = true)
public class NotifType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code")
    private NotificationTypeStatus code;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    private Notification notification;
}
