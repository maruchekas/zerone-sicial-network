package com.skillbox.javapro21.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dialog")
    private Set<Message> messages;

    @Column(name = "is_blocked")
    private int isBlocked;

    @Column(name = "code")
    private String code;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person2dialog",
            joinColumns = {@JoinColumn(name = "dialog_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")})
    @ToString.Exclude
    private Set<Person> persons;
}
