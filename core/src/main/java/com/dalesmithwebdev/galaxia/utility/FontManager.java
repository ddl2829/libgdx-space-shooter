package com.dalesmithwebdev.galaxia.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages dynamic TTF font generation and caching for the game.
 * Uses FreeTypeFontGenerator to create resolution-independent fonts
 * with effects like borders and shadows.
 */
public class FontManager {
    // Map font aliases to file paths for easy reference
    private static final Map<String, String> fontMapping = new HashMap<String, String>() {{
        put("future", "fonts/kenvector_future.ttf");
        put("future_thin", "fonts/kenvector_future_thin.ttf");
        put("lato", "fonts/Lato-Regular.ttf");
        put("lato_bold", "fonts/Lato-Bold.ttf");
        put("lato_light", "fonts/Lato-Light.ttf");
        put("lato_black", "fonts/Lato-Black.ttf");
        put("mono", "fonts/MonoRegular.ttf");
    }};

    // Cache generated label styles to avoid regenerating
    private static final Map<String, Label.LabelStyle> styleCache = new HashMap<>();

    // Glyph layout for text measurement
    private static final GlyphLayout layout = new GlyphLayout();

    /**
     * Create a label style from TTF font with custom parameters
     * @param styleName Unique identifier for this style
     * @param fontAlias Font alias from fontMapping
     * @param parameters FreeTypeFontParameter configuration
     */
    public static void createLabelStyle(String styleName, String fontAlias, FreeTypeFontParameter parameters) {
        // Load TTF file
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal(fontMapping.get(fontAlias))
        );

        // Generate BitmapFont from TTF
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = generator.generateFont(parameters);

        // Cache for reuse
        styleCache.put(styleName, style);

        // IMPORTANT: Dispose generator after font generation
        generator.dispose();
    }

    /**
     * Get a cached label style
     */
    public static Label.LabelStyle getLabelStyle(String styleName) {
        return styleCache.get(styleName);
    }

    /**
     * Calculate text width for layout calculations
     */
    public static float getTextWidth(String text, String styleName) {
        Label.LabelStyle style = getLabelStyle(styleName);
        if (style == null || style.font == null) {
            return 0;
        }
        layout.setText(style.font, text);
        return layout.width;
    }

    /**
     * Calculate text height for layout calculations
     */
    public static float getTextHeight(String text, String styleName) {
        Label.LabelStyle style = getLabelStyle(styleName);
        if (style == null || style.font == null) {
            return 0;
        }
        layout.setText(style.font, text);
        return layout.height;
    }

    /**
     * Scale font size based on screen height
     * Maintains proportional sizing across different resolutions (1080p base)
     */
    public static int scaleByScreenHeight(int baseSize) {
        int screenHeight = Gdx.graphics.getHeight();
        if (screenHeight == 1080) {
            return baseSize;  // No scaling needed at 1080p
        }

        if (screenHeight > 1080) {
            // Scale up for higher resolutions
            return (int) (baseSize * (1 + (screenHeight - 1080) / 1080f));
        } else {
            // Scale down for lower resolutions
            return (int) (baseSize * (1 - (1080 - screenHeight) / 1080f));
        }
    }

    /**
     * Dispose all generated fonts when shutting down
     */
    public static void dispose() {
        for (Label.LabelStyle style : styleCache.values()) {
            if (style.font != null) {
                style.font.dispose();
            }
        }
        styleCache.clear();
    }

    /**
     * Initialize all game fonts
     * Should be called once during game startup
     */
    public static void initializeFonts() {
        // Title font: large, sci-fi style with glow effect
        createLabelStyle("title", "future", new FreeTypeFontParameter() {{
            size = scaleByScreenHeight(96);
            borderColor = new Color(0, 0.5f, 0.8f, 1);  // Cyan outline
            borderWidth = 3;
            color = new Color(0.8f, 0.95f, 1, 1);      // Light cyan/white
            shadowColor = new Color(0, 0.6f, 1, 0.6f);  // Cyan glow
            shadowOffsetX = 0;
            shadowOffsetY = 0;
            minFilter = Texture.TextureFilter.Linear;
            magFilter = Texture.TextureFilter.Linear;
        }});

        // Menu items: medium-large, bold, with subtle outline
        createLabelStyle("menu_item", "lato_bold", new FreeTypeFontParameter() {{
            size = scaleByScreenHeight(40);
            borderColor = Color.BLACK;
            borderWidth = 2;
            color = Color.WHITE;
            minFilter = Texture.TextureFilter.Linear;
            magFilter = Texture.TextureFilter.Linear;
        }});

        // Menu items selected: same but with yellow color
        createLabelStyle("menu_item_selected", "lato_bold", new FreeTypeFontParameter() {{
            size = scaleByScreenHeight(40);
            borderColor = Color.BLACK;
            borderWidth = 2;
            color = new Color(1, 1, 0, 1);  // Yellow
            minFilter = Texture.TextureFilter.Linear;
            magFilter = Texture.TextureFilter.Linear;
        }});

        // HUD text: small, readable
        createLabelStyle("hud_text", "lato", new FreeTypeFontParameter() {{
            size = scaleByScreenHeight(20);
            borderColor = Color.BLACK;
            borderWidth = 1;
            color = Color.WHITE;
            minFilter = Texture.TextureFilter.Linear;
            magFilter = Texture.TextureFilter.Linear;
        }});

        // Score display: large, monospace, yellow
        createLabelStyle("score", "mono", new FreeTypeFontParameter() {{
            size = scaleByScreenHeight(32);
            borderColor = Color.BLACK;
            borderWidth = 2;
            color = new Color(1, 1, 0, 1);  // Yellow
            mono = true;
            minFilter = Texture.TextureFilter.Linear;
            magFilter = Texture.TextureFilter.Linear;
        }});
    }
}
