package com.natamus.screenshotcompression.compression.managers;

import com.natamus.screenshotcompression.data.Constants;
import com.natamus.screenshotcompression.util.Reference;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class QuantizationManager {
    public static BufferedImage applyQuantization(BufferedImage image, int numColors) {
        List <Color> colors = extractColors(image);

        List <Color> quantizedColors = medianCutQuantization(colors, numColors);

        return applyQuantizedColors(image, quantizedColors);
    }

    private static List <Color> extractColors(BufferedImage image) {
        Set <Color> uniqueColors = new HashSet <> ();
        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y <image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                uniqueColors.add(color);
            }
        }
        return new ArrayList <> (uniqueColors);
    }

    private static List <Color> medianCutQuantization(List <Color> colors, int numColors) {
        List <RGBColor> rgbColors = new ArrayList <> ();
        for (Color color: colors) {
            rgbColors.add(new RGBColor(color.getRed(), color.getGreen(), color.getBlue()));
        }

        List <RGBColor> reducedColors = medianCut(rgbColors, numColors);

        List <Color> quantizedColors = new ArrayList <> ();
        for (RGBColor rgbColor: reducedColors) {
            quantizedColors.add(new Color(rgbColor.r(), rgbColor.g(), rgbColor.b()));
        }
        return quantizedColors;
    }

    private static List <RGBColor> medianCut(List <RGBColor> colors, int numColors) {
        if (colors.size() <= numColors) {
            return colors;
        }

        List <RGBColorRange> ranges = new ArrayList <> ();
        ranges.add(new RGBColorRange(colors));

        while (ranges.size() <numColors) {
            RGBColorRange largestRange = findLargestRange(ranges);
            ranges.remove(largestRange);
            ranges.addAll(largestRange.split());
        }

        List <RGBColor> reducedColors = new ArrayList <> ();
        for (RGBColorRange range: ranges) {
            reducedColors.add(range.getMedianColor());
        }

        return reducedColors;
    }

    private static RGBColorRange findLargestRange(List <RGBColorRange> ranges) {
        RGBColorRange largestRange = ranges.getFirst();
        for (RGBColorRange range: ranges) {
            if (range.getRangeSize()> largestRange.getRangeSize()) {
                largestRange = range;
            }
        }
        return largestRange;
    }

    private static BufferedImage applyQuantizedColors(BufferedImage image, List <Color> quantizedColors) {
        BufferedImage quantizedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y <image.getHeight(); y++) {
                Color originalColor = new Color(image.getRGB(x, y));

                Color closestColor = findClosestColor(originalColor, quantizedColors);
                quantizedImage.setRGB(x, y, closestColor.getRGB());
            }
        }

        return quantizedImage;
    }

    private static Color findClosestColor(Color targetColor, List <Color> palette) {
        Color closestColor = palette.getFirst();
        double closestDistance = getColorDistance(targetColor, closestColor);

        for (Color color: palette) {
            double distance = getColorDistance(targetColor, color);
            if (distance <closestDistance) {
                closestColor = color;
                closestDistance = distance;
            }
        }

        return closestColor;
    }

    private static double getColorDistance(Color c1, Color c2) {
        int rDiff = c1.getRed() - c2.getRed();
        int gDiff = c1.getGreen() - c2.getGreen();
        int bDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    private record RGBColor(int r, int g, int b) {}

    private record RGBColorRange(List <RGBColor> colors) {

        public Collection <? extends RGBColorRange> split() {
            int maxDimension = findMaxDimension();
            colors.sort(Comparator.comparingInt(color -> getDimensionValue(color, maxDimension)));

            int mid = colors.size() / 2;
            List <RGBColor> left = colors.subList(0, mid);
            List <RGBColor> right = colors.subList(mid, colors.size());

            List <RGBColorRange> ranges = new ArrayList <> ();
            ranges.add(new RGBColorRange(left));
            ranges.add(new RGBColorRange(right));

            return ranges;
        }

        public RGBColor getMedianColor() {
            int mid = colors.size() / 2;
            return colors.get(mid);
        }

        public int getRangeSize() {
            int rMin = Integer.MAX_VALUE, gMin = Integer.MAX_VALUE, bMin = Integer.MAX_VALUE;
            int rMax = Integer.MIN_VALUE, gMax = Integer.MIN_VALUE, bMax = Integer.MIN_VALUE;

            for (RGBColor color: colors) {
                rMin = Math.min(rMin, color.r());
                gMin = Math.min(gMin, color.g());
                bMin = Math.min(bMin, color.b());

                rMax = Math.max(rMax, color.r());
                gMax = Math.max(gMax, color.g());
                bMax = Math.max(bMax, color.b());
            }

            return (rMax - rMin) + (gMax - gMin) + (bMax - bMin);
        }

        private int findMaxDimension() {
            int rMin = Integer.MAX_VALUE, gMin = Integer.MAX_VALUE, bMin = Integer.MAX_VALUE;
            int rMax = Integer.MIN_VALUE, gMax = Integer.MIN_VALUE, bMax = Integer.MIN_VALUE;

            for (RGBColor color: colors) {
                rMin = Math.min(rMin, color.r());
                gMin = Math.min(gMin, color.g());
                bMin = Math.min(bMin, color.b());

                rMax = Math.max(rMax, color.r());
                gMax = Math.max(gMax, color.g());
                bMax = Math.max(bMax, color.b());
            }

            int rRange = rMax - rMin;
            int gRange = gMax - gMin;
            int bRange = bMax - bMin;

            if (rRange>= gRange && rRange>= bRange) return 0;
            if (gRange>= bRange) return 1;
            return 2;
        }

        private int getDimensionValue(RGBColor color, int dimension) {
            return switch (dimension) {
            case 0 -> color.r();
            case 1 -> color.g();
            case 2 -> color.b();
            default -> {
                Constants.logger.warn("[" + Reference.MOD_ID + "] Invalid dimension: {}", dimension);
                yield 0;
            }
            };
        }
    }
}