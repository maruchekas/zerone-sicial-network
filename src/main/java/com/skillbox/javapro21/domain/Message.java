package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.config.PostgreSQLEnumType;
import com.skillbox.javapro21.domain.enumeration.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Accessors(chain = true)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "message_text")
    private String messageText;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @Column(name = "read_status")
    private ReadStatus readStatus;

    @ManyToOne(fetch = FetchType.EAGER)
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
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne(fetch = FetchType.EAGER)
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
//    @JoinColumn(name = "person_id")
    private Person recipient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Message message = (Message) o;
        return id != null && Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
