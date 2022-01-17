package com.skillbox.javapro21.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private int id;

    @Column(name = "city_id", nullable = false)
    private int cityId;

    @OneToMany(mappedBy = "country")
    private List<City> city;

    @Column(name = "name")
    private String name;
}
