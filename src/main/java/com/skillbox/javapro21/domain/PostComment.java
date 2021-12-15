package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @JsonIgnoreProperties(value = {"post", "comment", "person"}, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private BlockHistory block;

    @ManyToOne
    @JsonIgnoreProperties(value = {"block", "likes", "files", "comments", "tags", "author"}, allowSetters = true)
    private Post post;

    @ManyToOne
    @JsonIgnoreProperties(
            value = {"blocksLists",
                    "outFriendshipRequests",
                    "incFriendshipRequests",
                    "outMessages",
                    "incMessages",
                    "posts",
                    "postLikes",
                    "comments",
                    "notifications",},
            allowSetters = true)
    private Person person;
}
