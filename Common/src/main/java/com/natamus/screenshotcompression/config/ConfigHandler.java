package com.natamus.screenshotcompression.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.screenshotcompression.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean sendScreenshotCompressMessage = false;

	@Entry public static boolean keepOriginalScreenshot = false;

	@Entry(min = 0F, max = 1F) public static float compressionQuality = 0F;

	@Entry(min = 1, max = 100) public static int screenshotResizePercentage = 100;

	@Entry public static boolean stripPngAlphaChannel = true;

	@Entry public static boolean stripScreenshotMetadata = false;

	@Entry public static boolean reduceAmountOfColours = false;
	@Entry(min = 0, max = 256) public static int reducedColourAmount = 256;

	@Entry public static boolean reduceGradient = false;

	public static void initConfig() {
		configMetaData.put("sendScreenshotCompressMessage", Arrays.asList(
			"Whether a message should be sent in the chat of how much the screenshot size was compressed."
		));

		configMetaData.put("keepOriginalScreenshot", Arrays.asList(
			"If the original, uncompressed, screenshot should be saved in ./screenshots/original/."
		));

		configMetaData.put("compressionQuality", Arrays.asList(
			"The quality of the compressed screenshot. 0 is the strongest compression, 1 the weakest."
		));

		configMetaData.put("screenshotResizePercentage", Arrays.asList(
			"By default on 100%, so disabled. Resize taken screenshots to a lower resolution, while maintaining the aspect ratio. A lower number, means a lower file size."
		));

		configMetaData.put("stripPngAlphaChannel", Arrays.asList(
			"Whether the screenshot's alpha channel should be removed. This means the PNG doesn't support transparancy, but since Minecraft screenshots don't have that it's safe to use."
		));

		configMetaData.put("stripScreenshotMetadata", Arrays.asList(
			"If the screenshot metadata should be kept or not. When removes, it lowers the file size slightly."
		));

		configMetaData.put("reduceAmountOfColours", Arrays.asList(
			"Limits the number of colors used in the image to improve compression. When enabled, fewer colors are used, which reduces the file size but may alter the image’s appearance slightly."
		));
		configMetaData.put("reducedColourAmount", Arrays.asList(
			"The amount of colours that are used when 'reduceAmountOfColours' is enabled."
		));

		configMetaData.put("reduceGradient", Arrays.asList(
			"Controls the reduction of color gradients to improve compression. When enabled, it simplifies color transitions, either by smoothing or adding noise, to reduce file size while maintaining visual quality."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}