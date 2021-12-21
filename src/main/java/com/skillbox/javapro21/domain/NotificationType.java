package com.skillbox.javapro21.domain;

import lombok.*;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NotificationType that = (NotificationType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
