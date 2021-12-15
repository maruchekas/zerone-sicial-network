package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_type")
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_status")
    private NotificationTypeStatus notificationStatus;
}
