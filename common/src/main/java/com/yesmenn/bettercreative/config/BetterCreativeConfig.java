package com.yesmenn.bettercreative.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Loader-neutral definition of the client config. Every option knows its section, key, comment, default and
 * limits; the loader module binds it to a storage backend (NeoForge: {@code ModConfigSpec}, Fabric: JSON file).
 * Until it is bound, an option returns its default.
 */
public final class BetterCreativeConfig {

    private static final List<Option<?>> OPTIONS_MUTABLE = new ArrayList<>();

    /** All options in definition order; options of one section are contiguous. */
    public static final List<Option<?>> OPTIONS = Collections.unmodifiableList(OPTIONS_MUTABLE);

    /*
     * PANEL
     */

    public static final IntOption PANEL_WIDTH = intOption("panel", "panelWidth",
            "Width of the Better Creative Menu sidebar.", 140, 80, 300);
    public static final IntOption PANEL_HEIGHT = intOption("panel", "panelHeight",
            "Height of the Better Creative Menu sidebar.", 220, 100, 500);
    public static final IntOption PANEL_OFFSET_X = intOption("panel", "panelOffsetX",
            "Horizontal offset of the mod list panel.", 0, -500, 500);
    public static final IntOption PANEL_OFFSET_Y = intOption("panel", "panelOffsetY",
            "Vertical offset of the mod list panel.", 0, -500, 500);

    /*
     * TOGGLE BUTTON
     */

    public static final IntOption TOGGLE_BUTTON_X = intOption("toggleButton", "toggleButtonX",
            "Horizontal position of the toggle button relative to the default position.", 0, -200, 200);
    public static final IntOption TOGGLE_BUTTON_Y = intOption("toggleButton", "toggleButtonY",
            "Vertical position of the toggle button relative to the default position.", 0, -200, 200);
    public static final IntOption TOGGLE_BUTTON_SIZE = intOption("toggleButton", "toggleButtonSize",
            "Size of the toggle button.", 16, 8, 32);

    /*
     * APPEARANCE
     */

    public static final IntOption LIST_FONT_SIZE = intOption("appearance", "listFontSize",
            "Font size for the mod list panel text.", 9, 8, 16);
    public static final ColorOption FONT_COLOR = colorOption("appearance", "fontColor",
            "Color used for the font inside the panel.", "#FFFFFF");
    public static final IntOption FONT_ALPHA = intOption("appearance", "fontAlpha",
            "Opacity used for the font inside the panel.", 100, 0, 100);

    public static final ColorOption PANEL_BACKGROUND_COLOR = colorOption("appearance", "panelBackgroundColor",
            "Background color of the mod list panel in hexadecimal format.", "#202020");
    public static final IntOption PANEL_BACKGROUND_ALPHA = intOption("appearance", "panelBackgroundAlpha",
            "Background opacity of the mod list panel in percent.", 40, 0, 100);

    public static final ColorOption ROW_HOVER_COLOR = colorOption("appearance", "rowHoverColor",
            "Color used when hovering over a mod or creative tab.", "#404090");
    public static final IntOption ROW_HOVER_ALPHA = intOption("appearance", "rowHoverAlpha",
            "Hovered row opacity in percent.", 15, 0, 100);

    public static final ColorOption BORDER_COLOR = colorOption("appearance", "borderColor",
            "Border color of the panel.", "#404090");
    public static final IntOption BORDER_ALPHA = intOption("appearance", "borderAlpha",
            "Panel border opacity in percent.", 45, 0, 100);

    public static final ColorOption SEARCH_BOX_COLOR = colorOption("appearance", "searchBoxColor",
            "Background color of the search box.", "#404090");
    public static final IntOption SEARCH_BOX_ALPHA = intOption("appearance", "searchBoxAlpha",
            "Background opacity of the search box in percent.", 15, 0, 100);
    public static final IntOption SEARCH_BOX_MAX_WIDTH = intOption("appearance", "searchBoxMaxWidth",
            "Maximum width of the search box.", 180, 80, 300);

    private BetterCreativeConfig() {
    }

    private static IntOption intOption(String section, String key, String comment, int def, int min, int max) {
        IntOption option = new IntOption(section, key, comment, def, min, max);
        OPTIONS_MUTABLE.add(option);
        return option;
    }

    private static ColorOption colorOption(String section, String key, String comment, String def) {
        ColorOption option = new ColorOption(section, key, comment, def);
        OPTIONS_MUTABLE.add(option);
        return option;
    }

    public static boolean isValidColor(Object value) {
        return value instanceof String string && string.matches("^#[0-9a-fA-F]{6}$");
    }

    public static int colorWithAlpha(String color, int alphaPercent) {
        String value = color;

        if (!isValidColor(value)) {
            value = "#202020";
        }

        int rgb = Integer.parseInt(value.substring(1), 16);
        int alpha = Math.max(0, Math.min(100, alphaPercent));
        int alphaByte = Math.round(alpha * 255.0f / 100.0f);

        return (alphaByte << 24) | rgb;
    }

    /*
     * OPTION TYPES
     */

    public abstract static sealed class Option<T> permits IntOption, ColorOption {
        private final String section;
        private final String key;
        private final String comment;
        private final T defaultValue;
        private Supplier<T> source;

        Option(String section, String key, String comment, T defaultValue) {
            this.section = section;
            this.key = key;
            this.comment = comment;
            this.defaultValue = defaultValue;
        }

        public String section() {
            return section;
        }

        public String key() {
            return key;
        }

        public String comment() {
            return comment;
        }

        public T defaultValue() {
            return defaultValue;
        }

        public String translationKey() {
            return "ymbettercreativemenu.config." + key;
        }

        /** Connects the option to the loader's config storage. */
        public void bind(Supplier<T> source) {
            this.source = source;
        }

        public T get() {
            if (source == null) {
                return defaultValue;
            }
            T value = source.get();
            return value != null && isValid(value) ? value : defaultValue;
        }

        public abstract boolean isValid(Object value);

        /** Parses user input; returns {@code null} if it is not a valid value. */
        public abstract T parse(String text);
    }

    public static final class IntOption extends Option<Integer> {
        private final int min;
        private final int max;

        IntOption(String section, String key, String comment, int def, int min, int max) {
            super(section, key, comment, def);
            this.min = min;
            this.max = max;
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }

        @Override
        public boolean isValid(Object value) {
            return value instanceof Integer i && i >= min && i <= max;
        }

        @Override
        public Integer parse(String text) {
            try {
                int value = Integer.parseInt(text.trim());
                return isValid(value) ? value : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static final class ColorOption extends Option<String> {
        ColorOption(String section, String key, String comment, String def) {
            super(section, key, comment, def);
        }

        @Override
        public boolean isValid(Object value) {
            return isValidColor(value);
        }

        @Override
        public String parse(String text) {
            String value = text.trim();
            return isValidColor(value) ? value.toUpperCase(java.util.Locale.ROOT) : null;
        }
    }
}
