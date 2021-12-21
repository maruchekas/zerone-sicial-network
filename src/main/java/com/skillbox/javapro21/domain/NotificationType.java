package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "notification_type")
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
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
