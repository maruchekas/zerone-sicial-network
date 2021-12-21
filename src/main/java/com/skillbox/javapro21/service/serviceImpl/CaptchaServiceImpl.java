package com.skillbox.javapro21.service.serviceImpl;

import com.github.cage.Cage;
import com.skillbox.javapro21.api.response.captcha.CaptchaResponse;
import com.skillbox.javapro21.domain.CaptchaCode;
import com.skillbox.javapro21.repository.CaptchaRepository;
import com.skillbox.javapro21.service.CaptchaService;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class CaptchaServiceImpl implements CaptchaService {
    @Value("${captcha.width}")
    private int captchaWidth;
    @Value("${captcha.height}")
    private int captchaHeight;
    @Value("${captcha.image}")
    private String captchaImagePNG;
    @Value("${captcha.lifespanBySec}")
    private long captchaLifespan;

    private final CaptchaRepository captchaRepository;
    @Autowired
    public CaptchaServiceImpl(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    public CaptchaResponse getNewCaptcha() {
        Timestamp timeThreshold = Timestamp.valueOf(LocalDateTime.now()
                .minusSeconds(captchaLifespan));
        captchaRepository.deleteOldCaptcha(timeThreshold);
        Cage cage = new Cage();
        String code = cage.getTokenGenerator().next();
        BufferedImage captchaImage = cage.drawImage(code);
        byte[] bytesOfImage = compressImageAndGetByteArray(captchaImage);
        String encodeImage = Base64.getEncoder().encodeToString(bytesOfImage);
        String secret = String.valueOf(code.hashCode());
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secret);
        captchaCode.setTime(new Timestamp(System.currentTimeMillis()));
        captchaRepository.saveAndFlush(captchaCode);
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setImage(captchaImagePNG + encodeImage);
        captchaResponse.setSecretCode(secret);
        return captchaResponse;
    }

    private byte[] compressImageAndGetByteArray(BufferedImage image) {
        byte[] result = new byte[0];
        image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC,
                captchaWidth, captchaHeight, Scalr.OP_ANTIALIAS);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            result = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}