package com.skillbox.javapro21.repository;

import com.skillbox.javapro21.domain.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service("CaptchaRepository")
@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CaptchaCode captcha WHERE captcha.time <= ?1")
    void deleteOldCaptcha(Timestamp timeThreshold);

    CaptchaCode findBySecretCode(String secretCode);
}