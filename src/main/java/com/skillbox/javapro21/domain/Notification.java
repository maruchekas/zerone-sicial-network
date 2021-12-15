package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Column(name = "contact")
    private String contact;

    @JsonIgnoreProperties(value = { "notificatios" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private NotificationType notificationType;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "blocksLists",
            "outFriendshipRequests",
            "incFriendshipRequests",
            "outMessages",
            "incMessages",
            "posts",
            "postLikes",
            "comments",
            "notifications",
        },
        allowSetters = true
    )
    private Person person;

}
