package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.NotifType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotifTypeRepository extends JpaRepository<NotifType, Integer> {

}
