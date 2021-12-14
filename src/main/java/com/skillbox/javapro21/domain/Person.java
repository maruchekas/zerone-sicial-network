package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.Permission;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @JsonIgnoreProperties(value = { "post", "comment", "person" }, allowSetters = true)
    private Set<BlockHistory> blocksLists = new HashSet<>();

    @OneToMany(mappedBy = "srcPerson")
    @JsonIgnoreProperties(value = { "friendshipStatus", "srcPerson", "dstPerson" }, allowSetters = true)
    private Set<Friendship> outFriendshipRequests = new HashSet<>();

    @OneToMany(mappedBy = "dstPerson")
    @JsonIgnoreProperties(value = { "friendshipStatus", "srcPerson", "dstPerson" }, allowSetters = true)
    private Set<Friendship> incFriendshipRequests = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @JsonIgnoreProperties(value = { "author", "recipient" }, allowSetters = true)
    private Set<Message> outMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    @JsonIgnoreProperties(value = { "author", "recipient" }, allowSetters = true)
    private Set<Message> incMessages = new HashSet<>();

    @OneToMany(mappedBy = "author")
    @JsonIgnoreProperties(value = { "block", "likes", "files", "comments", "tags", "author" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "person", "post" }, allowSetters = true)
    private Set<PostLike> postLikes = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "block", "post", "person" }, allowSetters = true)
    private Set<PostComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonIgnoreProperties(value = { "type", "person" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();


    public Integer getId() {
        return this.id;
    }

    public Person id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Person firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Person lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ZonedDateTime getRegDate() {
        return this.regDate;
    }

    public Person regDate(ZonedDateTime regDate) {
        this.setRegDate(regDate);
        return this;
    }

    public void setRegDate(ZonedDateTime regDate) {
        this.regDate = regDate;
    }

    public ZonedDateTime getBirthDate() {
        return this.birthDate;
    }

    public Person birthDate(ZonedDateTime birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(ZonedDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return this.email;
    }

    public Person email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Person phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return this.password;
    }

    public Person password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return this.photo;
    }

    public Person photo(String photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAbout() {
        return this.about;
    }

    public Person about(String about) {
        this.setAbout(about);
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTown() {
        return this.town;
    }

    public Person town(String town) {
        this.setTown(town);
        return this;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return this.country;
    }

    public Person country(String country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getConfirmationCode() {
        return this.confirmationCode;
    }

    public Person confirmationCode(String confirmationCode) {
        this.setConfirmationCode(confirmationCode);
        return this;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Integer getIsApproved() {
        return this.isApproved;
    }

    public Person isApproved(Integer isApproved) {
        this.setIsApproved(isApproved);
        return this;
    }

    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public Permission getMessagesPermission() {
        return this.messagesPermission;
    }

    public Person messagesPermission(Permission messagesPermission) {
        this.setMessagesPermission(messagesPermission);
        return this;
    }

    public void setMessagesPermission(Permission messagesPermission) {
        this.messagesPermission = messagesPermission;
    }

    public ZonedDateTime getLastOnlineTime() {
        return this.lastOnlineTime;
    }

    public Person lastOnlineTime(ZonedDateTime lastOnlineTime) {
        this.setLastOnlineTime(lastOnlineTime);
        return this;
    }

    public void setLastOnlineTime(ZonedDateTime lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public Integer getIsBlocked() {
        return this.isBlocked;
    }

    public Person isBlocked(Integer isBlocked) {
        this.setIsBlocked(isBlocked);
        return this;
    }

    public void setIsBlocked(Integer isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Set<BlockHistory> getBlocksLists() {
        return this.blocksLists;
    }

    public void setBlocksLists(Set<BlockHistory> blockHistories) {
        if (this.blocksLists != null) {
            this.blocksLists.forEach(i -> i.setPerson(null));
        }
        if (blockHistories != null) {
            blockHistories.forEach(i -> i.setPerson(this));
        }
        this.blocksLists = blockHistories;
    }

    public Person blocksLists(Set<BlockHistory> blockHistories) {
        this.setBlocksLists(blockHistories);
        return this;
    }

    public Person addBlocksList(BlockHistory blockHistory) {
        this.blocksLists.add(blockHistory);
        blockHistory.setPerson(this);
        return this;
    }

    public Person removeBlocksList(BlockHistory blockHistory) {
        this.blocksLists.remove(blockHistory);
        blockHistory.setPerson(null);
        return this;
    }

    public Set<Friendship> getOutFriendshipRequests() {
        return this.outFriendshipRequests;
    }

    public void setOutFriendshipRequests(Set<Friendship> friendships) {
        if (this.outFriendshipRequests != null) {
            this.outFriendshipRequests.forEach(i -> i.setSrcPerson(null));
        }
        if (friendships != null) {
            friendships.forEach(i -> i.setSrcPerson(this));
        }
        this.outFriendshipRequests = friendships;
    }

    public Person outFriendshipRequests(Set<Friendship> friendships) {
        this.setOutFriendshipRequests(friendships);
        return this;
    }

    public Person addOutFriendshipRequests(Friendship friendship) {
        this.outFriendshipRequests.add(friendship);
        friendship.setSrcPerson(this);
        return this;
    }

    public Person removeOutFriendshipRequests(Friendship friendship) {
        this.outFriendshipRequests.remove(friendship);
        friendship.setSrcPerson(null);
        return this;
    }

    public Set<Friendship> getIncFriendshipRequests() {
        return this.incFriendshipRequests;
    }

    public void setIncFriendshipRequests(Set<Friendship> friendships) {
        if (this.incFriendshipRequests != null) {
            this.incFriendshipRequests.forEach(i -> i.setDstPerson(null));
        }
        if (friendships != null) {
            friendships.forEach(i -> i.setDstPerson(this));
        }
        this.incFriendshipRequests = friendships;
    }

    public Person incFriendshipRequests(Set<Friendship> friendships) {
        this.setIncFriendshipRequests(friendships);
        return this;
    }

    public Person addIncFriendshipRequests(Friendship friendship) {
        this.incFriendshipRequests.add(friendship);
        friendship.setDstPerson(this);
        return this;
    }

    public Person removeIncFriendshipRequests(Friendship friendship) {
        this.incFriendshipRequests.remove(friendship);
        friendship.setDstPerson(null);
        return this;
    }

    public Set<Message> getOutMessages() {
        return this.outMessages;
    }

    public void setOutMessages(Set<Message> messages) {
        if (this.outMessages != null) {
            this.outMessages.forEach(i -> i.setAuthor(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setAuthor(this));
        }
        this.outMessages = messages;
    }

    public Person outMessages(Set<Message> messages) {
        this.setOutMessages(messages);
        return this;
    }

    public Person addOutMessages(Message message) {
        this.outMessages.add(message);
        message.setAuthor(this);
        return this;
    }

    public Person removeOutMessages(Message message) {
        this.outMessages.remove(message);
        message.setAuthor(null);
        return this;
    }

    public Set<Message> getIncMessages() {
        return this.incMessages;
    }

    public void setIncMessages(Set<Message> messages) {
        if (this.incMessages != null) {
            this.incMessages.forEach(i -> i.setRecipient(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setRecipient(this));
        }
        this.incMessages = messages;
    }

    public Person incMessages(Set<Message> messages) {
        this.setIncMessages(messages);
        return this;
    }

    public Person addIncMessages(Message message) {
        this.incMessages.add(message);
        message.setRecipient(this);
        return this;
    }

    public Person removeIncMessages(Message message) {
        this.incMessages.remove(message);
        message.setRecipient(null);
        return this;
    }

    public Set<com.skillbox.javapro21.domain.Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<com.skillbox.javapro21.domain.Post> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.setAuthor(null));
        }
        if (posts != null) {
            posts.forEach(i -> i.setAuthor(this));
        }
        this.posts = posts;
    }

    public Person posts(Set<com.skillbox.javapro21.domain.Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public Person addPosts(com.skillbox.javapro21.domain.Post post) {
        this.posts.add(post);
        post.setAuthor(this);
        return this;
    }

    public Person removePosts(com.skillbox.javapro21.domain.Post post) {
        this.posts.remove(post);
        post.setAuthor(null);
        return this;
    }

    public Set<com.skillbox.javapro21.domain.PostLike> getPostLikes() {
        return this.postLikes;
    }

    public void setPostLikes(Set<com.skillbox.javapro21.domain.PostLike> postLikes) {
        if (this.postLikes != null) {
            this.postLikes.forEach(i -> i.setPerson(null));
        }
        if (postLikes != null) {
            postLikes.forEach(i -> i.setPerson(this));
        }
        this.postLikes = postLikes;
    }

    public Person postLikes(Set<com.skillbox.javapro21.domain.PostLike> postLikes) {
        this.setPostLikes(postLikes);
        return this;
    }

    public Person addPostLikes(com.skillbox.javapro21.domain.PostLike postLike) {
        this.postLikes.add(postLike);
        postLike.setPerson(this);
        return this;
    }

    public Person removePostLikes(com.skillbox.javapro21.domain.PostLike postLike) {
        this.postLikes.remove(postLike);
        postLike.setPerson(null);
        return this;
    }

    public Set<com.skillbox.javapro21.domain.PostComment> getComments() {
        return this.comments;
    }

    public void setComments(Set<com.skillbox.javapro21.domain.PostComment> postComments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setPerson(null));
        }
        if (postComments != null) {
            postComments.forEach(i -> i.setPerson(this));
        }
        this.comments = postComments;
    }

    public Person comments(Set<com.skillbox.javapro21.domain.PostComment> postComments) {
        this.setComments(postComments);
        return this;
    }

    public Person addComments(com.skillbox.javapro21.domain.PostComment postComment) {
        this.comments.add(postComment);
        postComment.setPerson(this);
        return this;
    }

    public Person removeComments(com.skillbox.javapro21.domain.PostComment postComment) {
        this.comments.remove(postComment);
        postComment.setPerson(null);
        return this;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setPerson(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setPerson(this));
        }
        this.notifications = notifications;
    }

    public Person notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public Person addNotifications(Notification notification) {
        this.notifications.add(notification);
        notification.setPerson(this);
        return this;
    }

    public Person removeNotifications(Notification notification) {
        this.notifications.remove(notification);
        notification.setPerson(null);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", regDate='" + getRegDate() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", password='" + getPassword() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", about='" + getAbout() + "'" +
            ", town='" + getTown() + "'" +
            ", country='" + getCountry() + "'" +
            ", confirmationCode='" + getConfirmationCode() + "'" +
            ", isApproved=" + getIsApproved() +
            ", messagesPermission='" + getMessagesPermission() + "'" +
            ", lastOnlineTime='" + getLastOnlineTime() + "'" +
            ", isBlocked=" + getIsBlocked() +
            "}";
    }
}
