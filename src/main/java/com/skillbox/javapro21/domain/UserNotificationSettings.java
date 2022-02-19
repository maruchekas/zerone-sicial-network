package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.domain.enumeration.NotificationType;
import com.skillbox.javapro21.domain.marker.HavePerson;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "user_notification_settings")
@Accessors(chain = true)
public class UserNotificationSettings implements HavePerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "post")
    private boolean isPost;

    @Column(name = "post_comment")
    private boolean isPostComment;

    @Column(name = "comment_comment")
    private boolean isCommentComment;

    @Column(name = "friends_request")
    private boolean isFriendsRequest;

    @Column(name = "message")
    private boolean isMessage;

    @Column(name = "friends_birthday")
    private boolean isFriendsBirthday;
}
