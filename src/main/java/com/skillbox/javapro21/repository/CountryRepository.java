package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    Optional<Country> findCountryByName(String name);
}