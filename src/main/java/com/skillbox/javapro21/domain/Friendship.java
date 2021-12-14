package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonIgnoreProperties(value = { "friendship" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private FriendshipStatus friendshipStatus;

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
    private Person srcPerson;

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
    private Person dstPerson;


    public Integer getId() {
        return this.id;
    }

    public Friendship id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FriendshipStatus getFriendshipStatus() {
        return this.friendshipStatus;
    }

    public void setFriendshipStatus(FriendshipStatus friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    public Friendship friendshipStatus(FriendshipStatus friendshipStatus) {
        this.setFriendshipStatus(friendshipStatus);
        return this;
    }

    public Person getSrcPerson() {
        return this.srcPerson;
    }

    public void setSrcPerson(Person person) {
        this.srcPerson = person;
    }

    public Friendship srcPerson(Person person) {
        this.setSrcPerson(person);
        return this;
    }

    public Person getDstPerson() {
        return this.dstPerson;
    }

    public void setDstPerson(Person person) {
        this.dstPerson = person;
    }

    public Friendship dstPerson(Person person) {
        this.setDstPerson(person);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friendship)) {
            return false;
        }
        return id != null && id.equals(((Friendship) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Friendship{" +
            "id=" + getId() +
            "}";
    }
}
