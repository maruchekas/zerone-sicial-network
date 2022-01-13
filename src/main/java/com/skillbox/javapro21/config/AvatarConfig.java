package com.skillbox.javapro21.config;

import ij.ImagePlus;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;

public class AvatarConfig {

        public static void main(String[] args) throws IOException {
            String imagePath = "src/main/resources/assets/img/SKY_CIRCLE.jpg";

            ImagePlus resultGraphicsCentered = new ImagePlus("", signImageCenter(RandomStringUtils.randomAlphabetic(2).toUpperCase(), imagePath));
            resultGraphicsCentered.show();

        }

        /**
         * Draw a String centered in the middle of a Rectangle.
         *
         * @param g The Graphics instance.
         * @param text The String to draw.
         * @param rect The Rectangle to center the text in.
         * @throws IOException
         */
        public static BufferedImage signImageCenter(String text, String path) throws IOException {

            BufferedImage image = ImageIO.read(new File(path));
            Font font = new Font("Arial", Font.BOLD, 210);

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

