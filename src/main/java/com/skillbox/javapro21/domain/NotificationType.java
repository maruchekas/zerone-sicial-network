package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.api.response.Content;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "notification_type")
@Accessors(chain = true)
public class NotificationType {
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
}
