package com.natamus.screenshotcompression.compression.managers;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DitheringManager {
    public static BufferedImage applyDithering(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage imageCopy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Color oldColor = new Color(image.getRGB(x, y));

                Color newColor = quantizeColor(oldColor);

                imageCopy.setRGB(x, y, newColor.getRGB());

                int redError = oldColor.getRed() - newColor.getRed();
                int greenError = oldColor.getGreen() - newColor.getGreen();
                int blueError = oldColor.getBlue() - newColor.getBlue();

                if (x + 1 < width) {
                    Color rightColor = new Color(image.getRGB(x + 1, y));
                    imageCopy.setRGB(x + 1, y, getNewColorWithError(rightColor, redError, greenError, blueError, 7 / 16.0));
                }
                if (y + 1 < height) {
                    Color bottomLeftColor = new Color(image.getRGB(x - 1, y + 1));
                    imageCopy.setRGB(x - 1, y + 1, getNewColorWithError(bottomLeftColor, redError, greenError, blueError, 3 / 16.0));
                    Color bottomColor = new Color(image.getRGB(x, y + 1));
                    imageCopy.setRGB(x, y + 1, getNewColorWithError(bottomColor, redError, greenError, blueError, 5 / 16.0));
                    if (x + 1 < width) {
                        Color bottomRightColor = new Color(image.getRGB(x + 1, y + 1));
                        imageCopy.setRGB(x + 1, y + 1, getNewColorWithError(bottomRightColor, redError, greenError, blueError, 1 / 16.0));
                    }
                }
            }
        }

        return imageCopy;
    }

    private static Color quantizeColor(Color oldColor) {
        int r = (oldColor.getRed() / 32) * 32;
        int g = (oldColor.getGreen() / 32) * 32;
        int b = (oldColor.getBlue() / 32) * 32;

        return new Color(r, g, b);
    }

    private static int getNewColorWithError(Color color, int redError, int greenError, int blueError, double weight) {
        int red = Math.min(Math.max(color.getRed() + (int)(redError * weight), 0), 255);
        int green = Math.min(Math.max(color.getGreen() + (int)(greenError * weight), 0), 255);
        int blue = Math.min(Math.max(color.getBlue() + (int)(blueError * weight), 0), 255);
        return new Color(red, green, blue).getRGB();
    }
}
