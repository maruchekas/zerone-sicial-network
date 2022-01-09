package com.skillbox.javapro21.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "dialogs")
@Accessors(chain = true)
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Message> messages;

    @Column(name = "is_blocked")
    private int isBlocked;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "person2dialog",
            joinColumns = {@JoinColumn(name = "dialog_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")})
    private Set<Person> persons;
}
