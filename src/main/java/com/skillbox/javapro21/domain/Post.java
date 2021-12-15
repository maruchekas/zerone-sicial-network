package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "title")
    private String title;

    @Column(name = "post_text")
    private String postText;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @JsonIgnoreProperties(value = { "post", "comment", "person" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private BlockHistory block;

    @OneToMany(mappedBy = "post")
    @JsonIgnoreProperties(value = { "person", "post" }, allowSetters = true)
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "post")
    @JsonIgnoreProperties(value = { "post" }, allowSetters = true)
    private Set<PostFile> files = new HashSet<>();

    @OneToMany(mappedBy = "post")
    @JsonIgnoreProperties(value = { "block", "post", "person" }, allowSetters = true)
    private Set<PostComment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "post2tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnoreProperties(value = { "posts" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

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
    private Person author;

}
