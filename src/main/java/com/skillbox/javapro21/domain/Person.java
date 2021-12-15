package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.Permission;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "reg_date")
    private ZonedDateTime regDate;

    @Column(name = "birth_date")
    private ZonedDateTime birthDate;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "photo")
    private String photo;

    @Column(name = "about")
    private String about;

    @Column(name = "town")
    private String town;

    @Column(name = "country")
    private String country;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "is_approved")
    private Integer isApproved;

    @Enumerated(EnumType.STRING)
    @Column(name = "messages_permission")
    private Permission messagesPermission;

    @Column(name = "last_online_time")
    private ZonedDateTime lastOnlineTime;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = {"post", "comment", "person"}, allowSetters = true)
    private Set<BlockHistory> blocksLists = new HashSet<>();

    @OneToMany(mappedBy = "srcPerson")
    @JsonIgnoreProperties(value = {"friendshipStatus", "srcPerson", "dstPerson"}, allowSetters = true)
    private Set<Friendship> outFriendshipRequests = new HashSet<>();

    @OneToMany(mappedBy = "dstPerson")
    @JsonIgnoreProperties(value = {"friendshipStatus", "srcPerson", "dstPerson"}, allowSetters = true)
    private Set<Friendship> incFriendshipRequests = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @JsonIgnoreProperties(value = {"author", "recipient"}, allowSetters = true)
    private Set<Message> outMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    @JsonIgnoreProperties(value = {"author", "recipient"}, allowSetters = true)
    private Set<Message> incMessages = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @JsonIgnoreProperties(value = {"block", "likes", "files", "comments", "tags", "author"}, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = {"person", "post"}, allowSetters = true)
    private Set<PostLike> postLikes = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = {"block", "post", "person"}, allowSetters = true)
    private Set<PostComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = {"type", "person"}, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();
}
