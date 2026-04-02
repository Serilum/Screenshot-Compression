package com.natamus.screenshotcompression.compression.managers;

import com.natamus.screenshotcompression.data.Constants;
import com.natamus.screenshotcompression.util.Reference;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageResizeManager {
	public static BufferedImage resize(BufferedImage original, int percentage) {
		if (percentage <= 0 || percentage > 100) {
			Constants.logger.warn("[" + Reference.MOD_ID + "] " + "Resize percentage must be between 1 and 100");
			return original;
		}

		int newWidth = original.getWidth() * percentage / 100;
		int newHeight = original.getHeight() * percentage / 100;

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, original.getType());
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
		g2d.dispose();

		return resizedImage;
	}
}
