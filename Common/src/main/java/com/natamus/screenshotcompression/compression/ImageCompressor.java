package com.natamus.screenshotcompression.compression;

import com.natamus.screenshotcompression.compression.managers.DitheringManager;
import com.natamus.screenshotcompression.compression.managers.ImageResizeManager;
import com.natamus.screenshotcompression.compression.managers.QuantizationManager;
import com.natamus.screenshotcompression.compression.managers.StripAlphaManager;
import com.natamus.screenshotcompression.config.ConfigHandler;
import com.natamus.screenshotcompression.data.Constants;
import com.natamus.screenshotcompression.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Iterator;

public class ImageCompressor {
    public static void runScreenshotCompressThread(File screenshotFile) {
        new Thread(() -> {
            int attempts = 0;
            while (attempts < 20) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}

                if (!screenshotFile.exists() || !isValidImage(screenshotFile)) {
                    attempts++;
                    continue;
                }

                try {
                    ImageCompressor.compressPNG(screenshotFile);
                    break;
                } catch (IOException ex) {
                    Constants.logger.warn("[" + Reference.MOD_ID + "] Unable to compress screenshot.");
                    break;
                }
            }
        }).start();
    }

    private static void compressPNG(File inputFile) throws IOException {
        if (!inputFile.exists()) {
            Constants.logger.warn("[" + Reference.MOD_ID + "] " + "Input file does not exist: {}", inputFile.getAbsolutePath());
            return;
        }

        double inputSize = (double)inputFile.length();
        if (ConfigHandler.keepOriginalScreenshot) {
            File originalFolder = new File(inputFile.getParent(), "original");
            if (!originalFolder.exists()) {
                boolean ignored = originalFolder.mkdirs();
            }
            File backupFile = new File(originalFolder, inputFile.getName());
            Files.copy(inputFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        String inputPath = inputFile.getPath();
        BufferedImage image = ImageIO.read(inputFile);

        boolean ignored = inputFile.delete();

        if (image == null) {
            Constants.logger.warn("[" + Reference.MOD_ID + "] " + "Failed to load the image. It might not be a valid PNG: {}", inputFile.getAbsolutePath());
            return;
        }

        if (ConfigHandler.screenshotResizePercentage != 100) {
            image = ImageResizeManager.resize(image, ConfigHandler.screenshotResizePercentage);
        }

        if (ConfigHandler.reduceGradient) {
            image = DitheringManager.applyDithering(image);
        }

        if (ConfigHandler.reduceAmountOfColours) {
            image = QuantizationManager.applyQuantization(image, ConfigHandler.reducedColourAmount);
        }

        if (ConfigHandler.stripPngAlphaChannel) {
            if (image.getColorModel().hasAlpha()) {
                image = StripAlphaManager.stripAlphaChannel(image);
            }
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("PNG");
        ImageWriter writer = null;
        if (writers.hasNext()) {
            writer = writers.next();
        }

        if (writer == null) {
            Constants.logger.warn("[" + Reference.MOD_ID + "] " + "No PNG writer found!");
            return;
        }

        IIOMetadata metadata = null;
        if (!ConfigHandler.stripScreenshotMetadata) {
            metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(image), null);
        }

        File outputFile;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(ConfigHandler.compressionQuality);

                writer.write(null, new javax.imageio.IIOImage(image, null, metadata), param);
            }

            outputFile = new File(inputPath);
            try (ImageOutputStream outputIOS = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(outputIOS);
                writer.write(null, new javax.imageio.IIOImage(image, null, metadata), null);
            }
        }

        if (!ConfigHandler.sendScreenshotCompressMessage) {
            return;
        }

        double outputSize = (double)outputFile.length();
        String percentage = (new DecimalFormat("#.00")).format((1 - Math.abs((outputSize - inputSize) / inputSize)) * 100);
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal(" > Compressed screenshot output size: ").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal(percentage + "%").withStyle(ChatFormatting.GRAY)));
    }

    private static boolean isValidImage(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (IOException ex) {
            return false;
        }
    }
}