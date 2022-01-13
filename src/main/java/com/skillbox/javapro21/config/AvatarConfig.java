package com.skillbox.javapro21.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.skillbox.javapro21.api.response.account.AvatarUploadData;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import ij.ImagePlus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Component
@Setter
@Getter
@AllArgsConstructor
public class AvatarConfig {

    @Value("${avatar.basic_url}")
    private static String robTemplate;
    @Value("${avatar.avatar_config}")
    private static String avatarConfig = ".png?size=360x360";

    private final PersonRepository personRepository;
    private final Cloudinary cloudinary;

        public static String createDefaultRoboticAvatar(String username){
            int setNum = new Random().nextInt(4);
            String randomString = RandomStringUtils.randomAlphabetic(5);

            return Constants.BASE_ROBOTIC_AVA_URL + username + randomString + Constants.AVATAR_CONFIG + setNum;
        }


    public String saveDefaultAvatarToPerson(Person person) throws IOException {

        BufferedImage originalImage = AvatarConfig.createDefaultAvatar();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        baos.flush();

        MultipartFile multipartFile = new MultipartImage(baos.toByteArray());

        return saveUserAvatar(multipartFile, person).getRelativeFilePath();
    }

    public static BufferedImage createDefaultAvatar() throws IOException {
        String imagePath = "src/main/resources/assets/img/SKY_CIRCLE.jpg";

        ImagePlus resultGraphicsCentered = new ImagePlus("", signImageCenter(RandomStringUtils.randomAlphabetic(2).toUpperCase(), imagePath));
        resultGraphicsCentered.show();
        ImageIO.write(resultGraphicsCentered.getBufferedImage(), "jpg", new File("src/main/resources/assets//cimg/Image-created.jpg"));

        return resultGraphicsCentered.getBufferedImage();

    }

    private AvatarUploadData saveUserAvatar(MultipartFile image, Person person) throws IOException {
        Map params = ObjectUtils.asMap(
                "public_id", Constants.CLOUDINARY_AVATARS_FOLDER + person.getEmail(),
                "transformation", new Transformation<>().width(1024).height(1024)
        );
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), params);
        String url = uploadResult.get("url").toString();
        AvatarUploadData data = new AvatarUploadData()
                .setOwnerId(person.getId())
                .setFileName(image.getOriginalFilename())
                .setRelativeFilePath(url)
                .setRawFileURL(url)
                .setFileFormat(image.getContentType())
                .setBytes(image.getSize())
                .setCreatedAt(new Date().getTime());
        person.setPhoto(url);
        personRepository.save(person);
        return data;
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param 'g'    The Graphics instance.
     * @param 'text' The String to draw.
     * @throws IOException
     */
    public static BufferedImage signImageCenter(String text, String path) throws IOException {

        BufferedImage image = ImageIO.read(new File(path));
        Font font = new Font("Arial", Font.BOLD, 180);

        AttributedString attributedText = new AttributedString(text);
        attributedText.addAttribute(TextAttribute.FONT, font);
        attributedText.addAttribute(TextAttribute.FOREGROUND, Color.orange);

        Graphics g = image.getGraphics();

        FontMetrics metrics = g.getFontMetrics(font);
        int positionX = (image.getWidth() - metrics.stringWidth(text)) / 2;
        int positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(attributedText.getIterator(), positionX, positionY);

        return image;
    }

}

