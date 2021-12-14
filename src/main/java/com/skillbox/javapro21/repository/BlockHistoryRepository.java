package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.BlockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockHistoryRepository extends JpaRepository<BlockHistory, Integer> {



}
