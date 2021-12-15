package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonIgnoreProperties(value = {"friendship"}, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private FriendshipStatus friendshipStatus;

    @ManyToOne
    @JsonIgnoreProperties(
            value = {"blocksLists",
                    "outFriendshipRequests",
                    "incFriendshipRequests",
                    "outMessages",
                    "incMessages",
                    "posts",
                    "postLikes",
                    "comments",
                    "notifications",},
            allowSetters = true)
    private Person srcPerson;

    @ManyToOne
    @JsonIgnoreProperties(
            value = {"blocksLists",
                    "outFriendshipRequests",
                    "incFriendshipRequests",
                    "outMessages",
                    "incMessages",
                    "posts",
                    "postLikes",
                    "comments",
                    "notifications",},
            allowSetters = true)
    private Person dstPerson;
}
