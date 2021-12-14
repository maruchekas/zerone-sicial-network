package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "friendship_statuses")
public class FriendshipStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "friendship_status_type")
    private FriendshipStatusType friendshipStatusType;

    @JsonIgnoreProperties(value = { "friendshipStatus", "srcPerson", "dstPerson" }, allowSetters = true)
    @OneToOne(mappedBy = "friendshipStatus")
    private Friendship friendship;


    public Integer getId() {
        return this.id;
    }

    public FriendshipStatus id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public FriendshipStatus time(ZonedDateTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public FriendshipStatusType getFriendshipStatusType() {
        return this.friendshipStatusType;
    }

    public FriendshipStatus friendshipStatusType(FriendshipStatusType friendshipStatusType) {
        this.setFriendshipStatusType(friendshipStatusType);
        return this;
    }

    public void setFriendshipStatusType(FriendshipStatusType friendshipStatusType) {
        this.friendshipStatusType = friendshipStatusType;
    }

    public Friendship getFriendship() {
        return this.friendship;
    }

    public void setFriendship(Friendship friendship) {
        if (this.friendship != null) {
            this.friendship.setFriendshipStatus(null);
        }
        if (friendship != null) {
            friendship.setFriendshipStatus(this);
        }
        this.friendship = friendship;
    }

    public FriendshipStatus friendship(Friendship friendship) {
        this.setFriendship(friendship);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FriendshipStatus)) {
            return false;
        }
        return id != null && id.equals(((FriendshipStatus) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FriendshipStatus{" +
                "id=" + getId() +
                ", time='" + getTime() + "'" +
                ", friendshipStatusType='" + getFriendshipStatusType() + "'" +
                "}";
    }
}
