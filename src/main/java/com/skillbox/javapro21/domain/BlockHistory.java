package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.config.PostgreSQLEnumType;
import com.skillbox.javapro21.domain.enumeration.ActionType;
import com.skillbox.javapro21.domain.marker.HavePerson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Accessors(chain = true)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Table(name = "block_history")
public class BlockHistory implements HavePerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time")
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @Column(name = "action")
    private ActionType action;

    @JsonIgnoreProperties(value = {"block", "likes", "files", "comments", "tags", "author"}, allowSetters = true)
    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnoreProperties(value = {"block", "post", "person"}, allowSetters = true)
    @OneToOne
    @JoinColumn(name = "post_comment_id")
    private PostComment comment;

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
