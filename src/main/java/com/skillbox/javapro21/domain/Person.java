package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.config.PostgreSQLEnumType;
import com.skillbox.javapro21.domain.enumeration.MessagesPermission;
import com.skillbox.javapro21.domain.enumeration.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Entity
@Accessors(chain = true)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Table(name = "persons")
public class Person implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
    @Type(type = "pgsql_enum")
    @Column(name = "messages_permission")
    private MessagesPermission messagesPermission;

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "is_blocked")
    private Integer isBlocked;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @Column(name = "user_type")
    private UserType userType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "person2dialog",
            joinColumns = {@JoinColumn(name = "person_id")},
            inverseJoinColumns = {@JoinColumn(name = "dialog_id")})
    private Set<Dialog> dialogs;

//    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnoreProperties(value = {"post", "comment", "person"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<BlockHistory> blocksLists = new HashSet<>();
//
//    @OneToMany(mappedBy = "srcPerson")
//    @JsonIgnoreProperties(value = {"friendshipStatus", "srcPerson", "dstPerson"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<Friendship> outFriendshipRequests = new HashSet<>();
//
//    @OneToMany(mappedBy = "dstPerson")
//    @JsonIgnoreProperties(value = {"friendshipStatus", "srcPerson", "dstPerson"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<Friendship> incFriendshipRequests = new HashSet<>();
//
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    @JsonIgnoreProperties(value = {"author", "recipient"}, allowSetters = true)
    @ToString.Exclude
    private Set<Message> outMessages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "recipient")
    @JsonIgnoreProperties(value = {"author", "recipient"}, allowSetters = true)
    @ToString.Exclude
    private Set<Message> incMessages = new HashSet<>();
//
//    @OneToMany(mappedBy = "author")
//    @JsonIgnoreProperties(value = {"block", "likes", "files", "comments", "tags", "author"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<Post> posts = new HashSet<>();
//
//    @OneToMany(mappedBy = "person")
//    @JsonIgnoreProperties(value = {"person", "post"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<PostLike> postLikes = new HashSet<>();
//
//    @OneToMany(mappedBy = "person")
//    @JsonIgnoreProperties(value = {"block", "post", "person"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<PostComment> comments = new HashSet<>();
//
//    @OneToMany(mappedBy = "person")
//    @JsonIgnoreProperties(value = {"type", "person"}, allowSetters = true)
//    @ToString.Exclude
//    private Set<Notification> notifications = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Person person = (Person) o;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getUserType().getAuthorities();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isBlocked == 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isBlocked == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this.isApproved == 1;
    }
}
