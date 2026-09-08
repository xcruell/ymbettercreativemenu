package com.yesmenn.bettercreative.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class BetterCreativeConfig {

    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.IntValue PANEL_WIDTH;
    public static final ModConfigSpec.IntValue PANEL_HEIGHT;
    public static final ModConfigSpec.IntValue PANEL_OFFSET_X;
    public static final ModConfigSpec.IntValue PANEL_OFFSET_Y;

    public static final ModConfigSpec.IntValue TOGGLE_BUTTON_X;
    public static final ModConfigSpec.IntValue TOGGLE_BUTTON_Y;
    public static final ModConfigSpec.IntValue TOGGLE_BUTTON_SIZE;

    public static final ModConfigSpec.IntValue LIST_FONT_SIZE;
    public static final ModConfigSpec.ConfigValue<String>
            FONT_COLOR;
    public static final ModConfigSpec.IntValue
            FONT_ALPHA;

    public static final ModConfigSpec.ConfigValue<String>
            PANEL_BACKGROUND_COLOR;
    public static final ModConfigSpec.IntValue
            PANEL_BACKGROUND_ALPHA;

    public static final ModConfigSpec.ConfigValue<String>
            ROW_HOVER_COLOR;
    public static final ModConfigSpec.IntValue
            ROW_HOVER_ALPHA;

    public static final ModConfigSpec.ConfigValue<String>
            SEARCH_BOX_COLOR;

    public static final ModConfigSpec.ConfigValue<String>
            BORDER_COLOR;
    public static final ModConfigSpec.IntValue
            BORDER_ALPHA;

    public static final ModConfigSpec.IntValue
            SEARCH_BOX_ALPHA;

    public static final ModConfigSpec.IntValue
            SEARCH_BOX_MAX_WIDTH;

    static {

        ModConfigSpec.Builder builder =
                new ModConfigSpec.Builder();

        /*
         * PANEL
         */

        builder.push("panel");

        PANEL_WIDTH = builder
                .comment("Width of the Better Creative Menu sidebar.")
                .translation("ymbettercreativemenu.config.panelWidth")
                .defineInRange(
                        "panelWidth",
                        140,
                        80,
                        300
                );

        PANEL_HEIGHT = builder
                .comment("Height of the Better Creative Menu sidebar.")
                .translation("ymbettercreativemenu.config.panelHeight")
                .defineInRange(
                        "panelHeight",
                        220,
                        100,
                        500
                );

        PANEL_OFFSET_X = builder
                .comment("Horizontal offset of the mod list panel.")
                .translation("ymbettercreativemenu.config.panelOffsetX")
                .defineInRange(
                        "panelOffsetX",
                        0,
                        -500,
                        500
                );

        PANEL_OFFSET_Y = builder
                .comment("Vertical offset of the mod list panel.")
                .translation("ymbettercreativemenu.config.panelOffsetY")
                .defineInRange(
                        "panelOffsetY",
                        0,
                        -500,
                        500
                );

        builder.pop();

        /*
         * TOGGLE BUTTON
         */

        builder.push("toggleButton");

        TOGGLE_BUTTON_X = builder
                .comment(
                        "Horizontal position of the toggle button relative to the default position."
                )
                .translation("ymbettercreativemenu.config.toggleButtonX")
                .defineInRange(
                        "toggleButtonX",
                        0,
                        -200,
                        200
                );

        TOGGLE_BUTTON_Y = builder
                .comment(
                        "Vertical position of the toggle button relative to the default position."
                )
                .translation("ymbettercreativemenu.config.toggleButtonY")
                .defineInRange(
                        "toggleButtonY",
                        0,
                        -200,
                        200
                );

        TOGGLE_BUTTON_SIZE = builder
                .comment("Size of the toggle button.")
                .translation("ymbettercreativemenu.config.toggleButtonSize")
                .defineInRange(
                        "toggleButtonSize",
                        16,
                        8,
                        32
                );

        builder.pop();

        /*
         * APPEARANCE
         */

        builder.push("appearance");

        LIST_FONT_SIZE = builder
                .comment(
                        "Font size for the mod list panel text."
                )
                .translation(
                        "ymbettercreativemenu.config.listFontSize"
                )
                .defineInRange(
                        "listFontSize",
                        9,
                        8,
                        16
                );

        FONT_COLOR = builder
                .comment(
                        "Color used for the font inside the panel."
                )
                .translation(
                        "ymbettercreativemenu.config.fontColor"
                )
                .define(
                        "fontColor",
                        "#FFFFFF",
                        BetterCreativeConfig::isValidColor
                );

        FONT_ALPHA = builder
                .comment(
                        "Opacity used for the font inside the panel."
                )
                .translation(
                        "ymbettercreativemenu.config.fontAlpha"
                )
                .defineInRange(
                        "fontAlpha",
                        100,
                        0,
                        100
                );

        PANEL_BACKGROUND_COLOR = builder
                .comment(
                        "Background color of the mod list panel in hexadecimal format."
                )
                .translation(
                        "ymbettercreativemenu.config.panelBackgroundColor"
                )
                .define(
                        "panelBackgroundColor",
                        "#202020",
                        BetterCreativeConfig::isValidColor
                );

        PANEL_BACKGROUND_ALPHA = builder
                .comment(
                        "Background opacity of the mod list panel in percent."
                )
                .translation(
                        "ymbettercreativemenu.config.panelBackgroundAlpha"
                )
                .defineInRange(
                        "panelBackgroundAlpha",
                        40,
                        0,
                        100
                );

        ROW_HOVER_COLOR = builder
                .comment(
                        "Color used when hovering over a mod or creative tab."
                )
                .translation(
                        "ymbettercreativemenu.config.rowHoverColor"
                )
                .define(
                        "rowHoverColor",
                        "#404090",
                        BetterCreativeConfig::isValidColor
                );

        ROW_HOVER_ALPHA = builder
                .comment(
                        "Hovered row opacity in percent."
                )
                .translation(
                        "ymbettercreativemenu.config.rowHoverAlpha"
                )
                .defineInRange(
                        "rowHoverAlpha",
                        15,
                        0,
                        100
                );


        BORDER_COLOR = builder
                .comment(
                        "Border color of the panel."
                )
                .translation(
                        "ymbettercreativemenu.config.borderColor"
                )
                .define(
                        "borderColor",
                        "#404090",
                        BetterCreativeConfig::isValidColor
                );

        BORDER_ALPHA = builder
                .comment(
                        "Panel border opacity in percent."
                )
                .translation(
                        "ymbettercreativemenu.config.borderAlpha"
                )
                .defineInRange(
                        "borderAlpha",
                        45,
                        0,
                        100
                );

        SEARCH_BOX_COLOR = builder
                .comment(
                        "Background color of the search box."
                )
                .translation(
                        "ymbettercreativemenu.config.searchBoxColor"
                )
                .define(
                        "searchBoxColor",
                        "#404090",
                        BetterCreativeConfig::isValidColor
                );

        SEARCH_BOX_ALPHA = builder
                .comment(
                        "Background opacity of the search box in percent."
                )
                .translation(
                        "ymbettercreativemenu.config.searchBoxAlpha"
                )
                .defineInRange(
                        "searchBoxAlpha",
                        15,
                        0,
                        100
                );

        SEARCH_BOX_MAX_WIDTH = builder
                .comment(
                        "Maximum width of the search box."
                )
                .translation(
                        "ymbettercreativemenu.config.searchBoxMaxWidth"
                )
                .defineInRange(
                        "searchBoxMaxWidth",
                        180,
                        80,
                        300
                );

        builder.pop();

        CLIENT_SPEC = builder.build();
    }

    private static boolean isValidColor(Object value) {

        if (!(value instanceof String string)) {
            return false;
        }

        return string.matches(
                "^#[0-9a-fA-F]{6}$"
        );
    }

    public static int colorWithAlpha(
            String color,
            int alphaPercent
    ) {

        String value = color;

        if (value == null ||
                !value.matches("^#[0-9a-fA-F]{6}$")) {
            value = "#202020";
        }

        int rgb =
                Integer.parseInt(
                        value.substring(1),
                        16
                );

        int alpha =
                Math.max(
                        0,
                        Math.min(
                                100,
                                alphaPercent
                        )
                );

        int alphaByte =
                Math.round(
                        alpha * 255.0f / 100.0f
                );

        return (alphaByte << 24) | rgb;
    }

    private BetterCreativeConfig() {
    }
}