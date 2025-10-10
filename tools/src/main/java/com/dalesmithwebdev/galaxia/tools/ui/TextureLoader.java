package com.dalesmithwebdev.galaxia.tools.ui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load texture atlas sprites as JavaFX Images
 * Parses .atlas file and extracts regions from PNG without libGDX native dependencies
 */
public class TextureLoader {
    private static Map<String, Image> imageCache = new HashMap<>();
    private static Map<String, AtlasRegion> atlasRegions = new HashMap<>();
    private static Image atlasImage;

    /**
     * Initialize from atlas file path
     */
    public static void initialize(String atlasFilePath) {
        try {
            // Load the PNG image
            String pngPath = atlasFilePath.replace(".atlas", ".png");
            atlasImage = new Image(new FileInputStream(pngPath));

            // Parse the atlas file
            parseAtlasFile(atlasFilePath);

            imageCache.clear();
            System.out.println("TextureLoader initialized with " + atlasRegions.size() + " regions");

            // Debug: Print first 10 region names
            System.out.println("Sample regions loaded:");
            atlasRegions.keySet().stream().limit(10).forEach(name ->
                System.out.println("  - " + name));
        } catch (Exception e) {
            System.err.println("Failed to initialize TextureLoader: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parse libGDX .atlas file format
     */
    private static void parseAtlasFile(String atlasPath) throws IOException {
        System.out.println("Parsing atlas file: " + atlasPath);

        try (BufferedReader reader = new BufferedReader(new FileReader(atlasPath))) {
            String line;
            String currentRegionName = null;
            Integer x = null, y = null, width = null, height = null;
            int lineNumber = 0;
            int regionsFound = 0;
            boolean debugFirstLines = true;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                // Debug first 20 lines
                if (debugFirstLines && lineNumber <= 20) {
                    System.out.println("Line " + lineNumber + ": [" + line + "] trimmed=[" + trimmed + "]");
                }
                if (lineNumber == 20) debugFirstLines = false;

                if (trimmed.isEmpty()) {
                    continue;
                }

                // Skip header lines (only non-indented lines at the top of the file)
                boolean isIndented = line.startsWith(" ") || line.startsWith("\t");
                if (!isIndented && (trimmed.startsWith("ArcadeShooter.png") ||
                    trimmed.startsWith("size:") ||
                    trimmed.startsWith("format:") ||
                    trimmed.startsWith("filter:") ||
                    trimmed.startsWith("repeat:"))) {
                    if (lineNumber <= 20) System.out.println("  -> Skipping header line");
                    continue;
                }

                // Region name (no indent, no colon)
                boolean isRegionName = !isIndented && !trimmed.contains(":");
                if (lineNumber <= 20) System.out.println("  -> isRegionName=" + isRegionName);

                if (isRegionName) {
                    // Save previous region if complete
                    if (currentRegionName != null && x != null && y != null && width != null && height != null) {
                        AtlasRegion region = new AtlasRegion(x, y, width, height);
                        atlasRegions.put(currentRegionName, region);
                        regionsFound++;
                        if (regionsFound <= 5) {
                            System.out.println("  Added region: " + currentRegionName + " at (" + x + "," + y + ") size " + width + "x" + height);
                        }
                    }

                    // Start new region
                    currentRegionName = trimmed;
                    x = y = width = height = null;
                    if (lineNumber <= 20) System.out.println("  -> New region: " + currentRegionName);
                    continue;
                }

                // Parse properties (indented lines with colons)
                boolean isProperty = currentRegionName != null && isIndented && trimmed.contains(":");
                if (lineNumber <= 20) System.out.println("  -> isProperty=" + isProperty + " currentRegion=" + currentRegionName);

                if (isProperty) {
                    String[] parts = trimmed.split(":", 2);
                    if (parts.length < 2) continue;

                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    try {
                        if (key.equals("xy")) {
                            String[] coords = value.split(",");
                            if (coords.length == 2) {
                                x = Integer.parseInt(coords[0].trim());
                                y = Integer.parseInt(coords[1].trim());
                                if (lineNumber <= 20) System.out.println("  -> Parsed xy: " + x + ", " + y);
                            }
                        } else if (key.equals("size")) {
                            String[] dims = value.split(",");
                            if (dims.length == 2) {
                                width = Integer.parseInt(dims[0].trim());
                                height = Integer.parseInt(dims[1].trim());
                                if (lineNumber <= 20) System.out.println("  -> Parsed size: " + width + ", " + height);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing numbers at line " + lineNumber + ": " + line);
                    }
                }
            }

            // Save last region
            if (currentRegionName != null && x != null && y != null && width != null && height != null) {
                AtlasRegion region = new AtlasRegion(x, y, width, height);
                atlasRegions.put(currentRegionName, region);
                regionsFound++;
            }

            System.out.println("Atlas parsing complete. Found " + regionsFound + " regions.");
        }
    }

    /**
     * Get a JavaFX Image for a texture region name
     */
    public static Image getImage(String regionName) {
        if (imageCache.containsKey(regionName)) {
            return imageCache.get(regionName);
        }

        if (atlasImage == null) {
            System.err.println("TextureLoader not initialized");
            return null;
        }

        AtlasRegion region = atlasRegions.get(regionName);
        if (region == null) {
            System.err.println("Texture region not found: '" + regionName + "'");
            System.err.println("Available regions: " + atlasRegions.size());
            // Show similar names for debugging
            atlasRegions.keySet().stream()
                .filter(name -> name.toLowerCase().contains(regionName.toLowerCase().substring(0, Math.min(3, regionName.length()))))
                .limit(5)
                .forEach(name -> System.err.println("  Did you mean: " + name));
            return null;
        }

        Image image = extractRegion(region);
        if (image != null) {
            imageCache.put(regionName, image);
        }

        return image;
    }

    /**
     * Extract a region from the atlas image
     */
    private static Image extractRegion(AtlasRegion region) {
        try {
            PixelReader pixelReader = atlasImage.getPixelReader();
            WritableImage subImage = new WritableImage(pixelReader,
                region.x, region.y, region.width, region.height);
            return subImage;
        } catch (Exception e) {
            System.err.println("Error extracting region: " + e.getMessage());
            return null;
        }
    }

    /**
     * Clear the image cache
     */
    public static void clearCache() {
        imageCache.clear();
    }

    /**
     * Simple data class for atlas regions
     */
    private static class AtlasRegion {
        int x, y, width, height;

        AtlasRegion(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
