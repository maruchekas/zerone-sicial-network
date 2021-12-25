package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.domain.enumeration.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Accessors(chain = true)
@Table(name = "block_history")
public class BlockHistory implements Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time")
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
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
