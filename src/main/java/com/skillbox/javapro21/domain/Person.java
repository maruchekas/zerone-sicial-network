package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.Permission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "persons")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
    private LocalDateTime regDate;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

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
    private MessagesPermission messagesPermission;

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "post", "comment", "person" }, allowSetters = true)
    @ToString.Exclude
    private Set<BlockHistory> blocksLists = new HashSet<>();

    @OneToMany(mappedBy = "srcPerson")
    @JsonIgnoreProperties(value = { "friendshipStatus", "srcPerson", "dstPerson" }, allowSetters = true)
    @ToString.Exclude
    private Set<Friendship> outFriendshipRequests = new HashSet<>();

    @OneToMany(mappedBy = "dstPerson")
    @JsonIgnoreProperties(value = { "friendshipStatus", "srcPerson", "dstPerson" }, allowSetters = true)
    @ToString.Exclude
    private Set<Friendship> incFriendshipRequests = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @JsonIgnoreProperties(value = { "author", "recipient" }, allowSetters = true)
    @ToString.Exclude
    private Set<Message> outMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    @JsonIgnoreProperties(value = { "author", "recipient" }, allowSetters = true)
    @ToString.Exclude
    private Set<Message> incMessages = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @JsonIgnoreProperties(value = { "block", "likes", "files", "comments", "tags", "author" }, allowSetters = true)
    @ToString.Exclude
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "person", "post" }, allowSetters = true)
    @ToString.Exclude
    private Set<PostLike> postLikes = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "block", "post", "person" }, allowSetters = true)
    @ToString.Exclude
    private Set<PostComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "type", "person" }, allowSetters = true)
    @ToString.Exclude
    private Set<Notification> notifications = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Person person = (Person) o;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
