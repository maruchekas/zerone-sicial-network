package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.FriendshipStatusType;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "friendship_statuses")
public class FriendshipStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "code")
    private long code;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private FriendshipStatusType friendshipStatusType;

//    @JsonIgnoreProperties(value = {"friendshipStatus", "srcPerson", "dstPerson"}, allowSetters = true)
//    @OneToOne(mappedBy = "friendshipStatus")
//    private Friendship friendship;

}
