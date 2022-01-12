package com.skillbox.javapro21.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private int id;


    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "region_id")
    private int regionId;

    @Column(name = "name")
    private String name;

}
