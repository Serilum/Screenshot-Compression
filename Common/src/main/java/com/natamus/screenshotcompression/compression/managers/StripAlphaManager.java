package com.natamus.screenshotcompression.compression.managers;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StripAlphaManager {
    public static BufferedImage stripAlphaChannel(BufferedImage image) {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = rgbImage.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rgbImage;
    }
}
