package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.ActionType;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "block_history")
public class BlockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionType action;

    @JsonIgnoreProperties(value = { "block", "likes", "files", "comments", "tags", "author" }, allowSetters = true)
    @OneToOne(mappedBy = "block")
    private Post post;

    @JsonIgnoreProperties(value = { "block", "post", "person" }, allowSetters = true)
    @OneToOne(mappedBy = "block")
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


    public Integer getId() {
        return this.id;
    }

    public BlockHistory id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public BlockHistory time(ZonedDateTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public ActionType getAction() {
        return this.action;
    }

    public BlockHistory action(ActionType action) {
        this.setAction(action);
        return this;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.setBlock(null);
        }
        if (post != null) {
            post.setBlock(this);
        }
        this.post = post;
    }

    public BlockHistory post(Post post) {
        this.setPost(post);
        return this;
    }

    public PostComment getComment() {
        return this.comment;
    }

    public void setComment(PostComment postComment) {
        if (this.comment != null) {
            this.comment.setBlock(null);
        }
        if (postComment != null) {
            postComment.setBlock(this);
        }
        this.comment = postComment;
    }

    public BlockHistory comment(PostComment postComment) {
        this.setComment(postComment);
        return this;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public BlockHistory person(Person person) {
        this.setPerson(person);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockHistory)) {
            return false;
        }
        return id != null && id.equals(((BlockHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BlockHistory{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            ", action='" + getAction() + "'" +
            "}";
    }
}
