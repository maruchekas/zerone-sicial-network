package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.api.response.Content;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Post implements Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "title")
    private String title;

    @Column(name = "post_text")
    private String postText;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @JsonIgnoreProperties(value = {"post", "comment", "person"}, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private BlockHistory block;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"person", "post"}, allowSetters = true)
    @ToString.Exclude
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"post"}, allowSetters = true)
    @ToString.Exclude
    private Set<PostFile> files = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"block", "post", "person"}, allowSetters = true)
    @ToString.Exclude
    private Set<PostComment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "post2tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnoreProperties(value = {"posts"}, allowSetters = true)
    @ToString.Exclude
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

    @JoinColumn(name = "author_id")
    private Person author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Post post = (Post) o;
        return id != null && Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

