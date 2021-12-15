package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @JsonIgnoreProperties(value = { "post", "comment", "person" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private BlockHistory block;

    @ManyToOne
    @JsonIgnoreProperties(value = { "block", "likes", "files", "comments", "tags", "author" }, allowSetters = true)
    private Post post;

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
