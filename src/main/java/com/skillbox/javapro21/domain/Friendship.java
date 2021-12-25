package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.api.response.Content;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Accessors(chain = true)
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JsonIgnoreProperties(value = {"friendship"}, allowSetters = true)
    @OneToOne
    @JoinColumn(name = "status_id", unique = true)
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

}
