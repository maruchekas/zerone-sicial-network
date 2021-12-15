package com.skillbox.javapro21.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "tag")
    private String tag;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnoreProperties(value = { "block", "likes", "files", "comments", "tags", "author" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

}
