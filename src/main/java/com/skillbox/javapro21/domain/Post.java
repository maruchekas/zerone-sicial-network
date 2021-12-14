package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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


    public Integer getId() {
        return this.id;
    }

    public Post id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public Post time(ZonedDateTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public String getTitle() {
        return this.title;
    }

    public Post title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostText() {
        return this.postText;
    }

    public Post postText(String postText) {
        this.setPostText(postText);
        return this;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public Integer getIsBlocked() {
        return this.isBlocked;
    }

    public Post isBlocked(Integer isBlocked) {
        this.setIsBlocked(isBlocked);
        return this;
    }

    public void setIsBlocked(Integer isBlocked) {
        this.isBlocked = isBlocked;
    }

    public BlockHistory getBlock() {
        return this.block;
    }

    public void setBlock(BlockHistory blockHistory) {
        this.block = blockHistory;
    }

    public Post block(BlockHistory blockHistory) {
        this.setBlock(blockHistory);
        return this;
    }

    public Set<com.skillbox.javapro21.domain.PostLike> getLikes() {
        return this.likes;
    }

    public void setLikes(Set<com.skillbox.javapro21.domain.PostLike> postLikes) {
        if (this.likes != null) {
            this.likes.forEach(i -> i.setPost(null));
        }
        if (postLikes != null) {
            postLikes.forEach(i -> i.setPost(this));
        }
        this.likes = postLikes;
    }

    public Post likes(Set<com.skillbox.javapro21.domain.PostLike> postLikes) {
        this.setLikes(postLikes);
        return this;
    }

    public Post addLikes(com.skillbox.javapro21.domain.PostLike postLike) {
        this.likes.add(postLike);
        postLike.setPost(this);
        return this;
    }

    public Post removeLikes(com.skillbox.javapro21.domain.PostLike postLike) {
        this.likes.remove(postLike);
        postLike.setPost(null);
        return this;
    }

    public Set<PostFile> getFiles() {
        return this.files;
    }

    public void setFiles(Set<PostFile> postFiles) {
        if (this.files != null) {
            this.files.forEach(i -> i.setPost(null));
        }
        if (postFiles != null) {
            postFiles.forEach(i -> i.setPost(this));
        }
        this.files = postFiles;
    }

    public Post files(Set<PostFile> postFiles) {
        this.setFiles(postFiles);
        return this;
    }

    public Post addFiles(PostFile postFile) {
        this.files.add(postFile);
        postFile.setPost(this);
        return this;
    }

    public Post removeFiles(PostFile postFile) {
        this.files.remove(postFile);
        postFile.setPost(null);
        return this;
    }

    public Set<PostComment> getComments() {
        return this.comments;
    }

    public void setComments(Set<PostComment> postComments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setPost(null));
        }
        if (postComments != null) {
            postComments.forEach(i -> i.setPost(this));
        }
        this.comments = postComments;
    }

    public Post comments(Set<PostComment> postComments) {
        this.setComments(postComments);
        return this;
    }

    public Post addComments(PostComment postComment) {
        this.comments.add(postComment);
        postComment.setPost(this);
        return this;
    }

    public Post removeComments(PostComment postComment) {
        this.comments.remove(postComment);
        postComment.setPost(null);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Post tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Post addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPosts().add(this);
        return this;
    }

    public Post removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPosts().remove(this);
        return this;
    }

    public Person getAuthor() {
        return this.author;
    }

    public void setAuthor(Person person) {
        this.author = person;
    }

    public Post author(Person person) {
        this.setAuthor(person);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            ", title='" + getTitle() + "'" +
            ", postText='" + getPostText() + "'" +
            ", isBlocked=" + getIsBlocked() +
            "}";
    }
}
