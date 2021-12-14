package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skillbox.javapro21.domain.enumeration.ReadStatus;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "message_text")
    private String messageText;

    @Enumerated(EnumType.STRING)
    @Column(name = "read_status")
    private ReadStatus readStatus;

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
    private Person recipient;


    public Integer getId() {
        return this.id;
    }

    public Message id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public Message time(ZonedDateTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public Message messageText(String messageText) {
        this.setMessageText(messageText);
        return this;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public ReadStatus getReadStatus() {
        return this.readStatus;
    }

    public Message readStatus(ReadStatus readStatus) {
        this.setReadStatus(readStatus);
        return this;
    }

    public void setReadStatus(ReadStatus readStatus) {
        this.readStatus = readStatus;
    }

    public Person getAuthor() {
        return this.author;
    }

    public void setAuthor(Person person) {
        this.author = person;
    }

    public Message author(Person person) {
        this.setAuthor(person);
        return this;
    }

    public Person getRecipient() {
        return this.recipient;
    }

    public void setRecipient(Person person) {
        this.recipient = person;
    }

    public Message recipient(Person person) {
        this.setRecipient(person);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", time='" + getTime() + "'" +
            ", messageText='" + getMessageText() + "'" +
            ", readStatus='" + getReadStatus() + "'" +
            "}";
    }
}
