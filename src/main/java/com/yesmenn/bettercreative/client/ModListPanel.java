package com.yesmenn.bettercreative.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.platform.InputConstants;
import com.yesmenn.bettercreative.config.BetterCreativeConfig;
import com.yesmenn.bettercreative.mixin.CreativeInventoryAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

public class ModListPanel {

    private static final int TAB_HEIGHT = 28 + 28;
    private static final int PADDING = 4;
    private static final int CHILD_INDENT = 10;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int SCROLLBAR_GAP = 3;
    private static final int BORDER_WIDTH = 1;
    private static final int SEARCH_CLEAR_SIZE = 8;
    private static final int SEARCH_CLEAR_GAP = PADDING * 2;
    private static final int STAR_CLICK_PADDING_BOTTOM = 3;
    private double scrollbarDragOffset = 0.0;

    private static final ResourceLocation STAR_FULL =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/star_full.png"
            );
    private static final ResourceLocation STAR_FULL_HOVERED =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/star_full_hover.png"
            );

    private static final ResourceLocation STAR_EMPTY_HOVERED =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/star_empty_hover.png"
            );
    private static final ResourceLocation STAR_EMPTY =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/star_empty.png"
            );

    private static final ResourceLocation EXPAND =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/expand.png"
            );
    private static final ResourceLocation EXPAND_HOVERED =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/expand_hover.png"
            );

    private static final ResourceLocation COLLAPSE =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/collapse.png"
            );
    private static final ResourceLocation COLLAPSE_HOVERED =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/collapse_hover.png"
            );

    private static final ResourceLocation SEARCH_CLEAR =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/search_clear.png"
            );
    private static final ResourceLocation SEARCH_CLEAR_HOVERED =
            ResourceLocation.parse(
                    "ymbettercreativemenu:textures/gui/search_clear_hover.png"
            );

    private static final int STAR_TEXTURE_SIZE = 16;

    private final List<ModEntry> mods = new ArrayList<>();

    private final Set<ResourceLocation> favorites =
            new HashSet<>();

    private final Set<String> collapsedMods =
            new HashSet<>();

    private int left;
    private int top;

    private double scrollOffset = 0.0;

    private EditBox searchBox;

    private boolean favoritesOnly = false;

    private boolean draggingScrollbar = false;

    private boolean visible = true;

    public boolean isVisible() {
        return visible;
    }

    public void toggleVisible() {
        setVisible(!visible);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;

        if (searchBox != null) {
            searchBox.visible = visible;
            searchBox.active = visible;
        }
    }

    public ModListPanel() {
        loadFavorites();
        rebuild();
    }

    /*
     * FONT / SIZE
     */

    public static int getFontSize() {
        return BetterCreativeConfig.LIST_FONT_SIZE.get();
    }

    public static int getFontColor() {
        return BetterCreativeConfig.colorWithAlpha(
                BetterCreativeConfig.FONT_COLOR.get(),
                BetterCreativeConfig.FONT_ALPHA.get()
        );
    }

    private int getContentStartY() {
        return top
                + getSearchRowHeight()
                + getSearchRowPadding();
    }

    private int getSearchBoxHeight() {
        return getFontSize() + 6;
    }

    private int getIconHeight() {
        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        return (int) (16 * fontSizeScale);
    }

    private int getScaledLineHeight() {
        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        return (int) (9 * fontSizeScale);
    }

    private int getSearchRowPadding() {
        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        return Math.max(
                2,
                Math.round(4 * fontSizeScale)
        );
    }

    private int calculateCenteredOffset(
            int rowHeight,
            int elementHeight
    ) {
        return (rowHeight - elementHeight) / 2;
    }

    private int calculateTextVerticalPosition(
            int rowHeight
    ) {
        return (rowHeight - getScaledLineHeight()) / 2;
    }

    private int calculateModRowHeight() {
        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        return (int) (22 * fontSizeScale);
    }

    private int calculateRowHeight() {
        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        return (int) (20 * fontSizeScale);
    }

    /*
     * CONFIG
     */

    private int getPanelWidth() {
        return BetterCreativeConfig.PANEL_WIDTH.get();
    }

    private int getPanelHeight() {
        return BetterCreativeConfig.PANEL_HEIGHT.get();
    }

    /*
     * SEARCH BOX
     */

    public EditBox getSearchBox() {
        return searchBox;
    }

    private int getSearchBoxColor() {
        return BetterCreativeConfig.colorWithAlpha(
                BetterCreativeConfig.SEARCH_BOX_COLOR.get(),
                BetterCreativeConfig.SEARCH_BOX_ALPHA.get()
        );
    }

    public EditBox createSearchBox(
            CreativeModeInventoryScreen screen
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();


        if (searchBox == null) {

            searchBox =
                    new ScaledSearchBox(
                            minecraft.font,
                            0,
                            0,
                            100,
                            getSearchBoxHeight(),
                            Component.translatable(
                                    "gui.ymbettercreativemenu.search.text"
                            ),
                            (float) getFontSize() / 12.0f
                    );

            searchBox.setBordered(false);

            searchBox.setHint(
                    Component.translatable("gui.ymbettercreativemenu.search.box")
            );

            searchBox.setResponder(
                    text -> scrollOffset = 0.0
            );

            searchBox.setCanLoseFocus(true);

            searchBox.visible = visible;
            searchBox.active = visible;
        }

        updatePosition(screen);
        updateSearchBoxPosition();

        return searchBox;
    }

    private int getSearchClearX() {
        return searchBox.getX()
                + searchBox.getWidth() + PADDING;
    }

    private int getSearchClearY() {
        return searchBox.getY()
                + (
                searchBox.getHeight()
                        - getSearchClearSize()
        ) / 2;
    }

    private int getSearchClearSize() {
        return getStarSize(
                getSearchBoxHeight()
        );
    }

    private void updateSearchBoxPosition() {

        if (searchBox == null) {
            return;
        }

        int width =
                getPanelWidth();

        int searchStarSize =
                getSearchStarSize();

        int searchStarX =
                getSearchStarX(
                        width,
                        searchStarSize
                );

        int searchX =
                left + PADDING;

        int availableSearchWidth =
                searchStarX
                        - SCROLLBAR_WIDTH
                        - SCROLLBAR_GAP
                        - SEARCH_CLEAR_GAP
                        - SEARCH_CLEAR_SIZE
                        - searchX;

        int configuredMaxWidth =
                BetterCreativeConfig.SEARCH_BOX_MAX_WIDTH.get();

        int searchWidth =
                Math.min(
                        availableSearchWidth,
                        configuredMaxWidth
                );

        searchBox.setX(searchX);

        searchBox.setY(
                top + PADDING
        );

        searchBox.setWidth(
                Math.max(
                        30,
                        searchWidth
                )
        );

        searchBox.setHeight(
                getSearchBoxHeight()
        );
    }

    /*
     * POSITION
     */

    private void updatePosition(
            CreativeModeInventoryScreen screen
    ) {

        int width =
                getPanelWidth();

        int panelHeight =
                getPanelHeight();

        this.left =
                screen.getGuiLeft()
                        - width
                        - 4
                        + BetterCreativeConfig.PANEL_OFFSET_X.get();

        int inventoryCenterY =
                screen.getGuiTop()
                        + 82;

        this.top =
                inventoryCenterY
                        - panelHeight / 2
                        - TAB_HEIGHT / 2
                        + BetterCreativeConfig.PANEL_OFFSET_Y.get();
    }

    private int getStarSize(
            int rowHeight
    ) {
        return Math.max(
                1,
                (int) (rowHeight * 0.8f)
        );
    }

    private int getModStarSize() {
        return getStarSize(
                calculateModRowHeight()
        );
    }

    private int getChildStarSize() {
        return getStarSize(
                calculateRowHeight()
        );
    }

    private int getChildStarX(int starSize) {
        return getStarX(starSize) + CHILD_INDENT;
    }

    private int getSearchStarSize() {
        return getStarSize(
                calculateModRowHeight()
        );
    }

    private int getStarX(
            int starSize
    ) {

        return left
                + getPanelWidth()
                - SCROLLBAR_WIDTH
                - starSize
                - 16;
    }

    private int getSearchStarX(
            int panelWidth,
            int starSize
    ) {

        return left
                + panelWidth
                - SCROLLBAR_WIDTH
                - starSize
                - 4;
    }

    private int getRowStarY(
            int rowY,
            int rowHeight,
            boolean childStar
    ) {
        int starSize =
                childStar
                        ? getChildStarSize()
                        : getModStarSize();

        return rowY
                + (rowHeight - starSize) / 2;
    }

    private int getSearchStarY() {

        int starSize =
                getSearchStarSize();

        return top
                + getSearchRowPadding()
                + calculateCenteredOffset(
                getSearchBoxHeight(),
                starSize
        );
    }

    private int getPanelBackgroundColor() {

        return BetterCreativeConfig.colorWithAlpha(
                BetterCreativeConfig.PANEL_BACKGROUND_COLOR.get(),
                BetterCreativeConfig.PANEL_BACKGROUND_ALPHA.get()
        );
    }

    private boolean isMouseOverRectangle(
            double mouseX,
            double mouseY,
            int x,
            int y,
            int width,
            int height
    ) {

        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    /*
     * ITEM RENDERING
     */

    private void renderItemScaled(
            GuiGraphics graphics,
            ItemStack item,
            int x,
            int rowY,
            int rowHeight
    ) {

        int iconHeight =
                getIconHeight();

        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        int centeredOffset =
                calculateCenteredOffset(
                        rowHeight,
                        iconHeight
                );

        graphics.pose().pushPose();

        graphics.pose().scale(
                fontSizeScale,
                fontSizeScale,
                1.0f
        );

        int scaledX =
                (int) (x / fontSizeScale);

        int scaledY =
                (int) (
                        (rowY + centeredOffset)
                                / fontSizeScale
                );

        graphics.renderItem(
                item,
                scaledX,
                scaledY
        );

        graphics.pose().popPose();
    }

    /*
     * BACKGROUND
     */

    private int getBorderColor() {
        return BetterCreativeConfig.colorWithAlpha(
                BetterCreativeConfig.BORDER_COLOR.get(),
                BetterCreativeConfig.BORDER_ALPHA.get()
        );
    }

    private int getRowHoverColor() {
        return BetterCreativeConfig.colorWithAlpha(
                BetterCreativeConfig.ROW_HOVER_COLOR.get(),
                BetterCreativeConfig.ROW_HOVER_ALPHA.get()
        );
    }

    public void renderBackground(
            GuiGraphics graphics,
            CreativeModeInventoryScreen screen
    ) {

        updatePosition(screen);

        int width =
                getPanelWidth();

        int panelHeight =
                getPanelHeight();

        int right =
                left + width;

        int bottom =
                top + panelHeight;

        /*
         * PANEL BACKGROUND
         */

        graphics.fill(
                left,
                top,
                right,
                bottom,
                getPanelBackgroundColor()
        );

        /*
         * PANEL BORDER
         */

        graphics.fill(
                left,
                top,
                right,
                top + BORDER_WIDTH,
                getBorderColor()
        );

        graphics.fill(
                left,
                bottom - BORDER_WIDTH,
                right,
                bottom,
                getBorderColor()
        );

        graphics.fill(
                left,
                top,
                left + BORDER_WIDTH,
                bottom,
                getBorderColor()
        );

        graphics.fill(
                right - BORDER_WIDTH,
                top,
                right,
                bottom,
                getBorderColor()
        );
    }

    /*
     * RENDER
     */
    private int getSearchRowHeight() {
        return getSearchBoxHeight()
                + getSearchRowPadding() * 2;
    }

    private int getContentTop() {
        return getSearchRowHeight();
    }

    public void render(
            GuiGraphics graphics,
            CreativeModeInventoryScreen screen,
            int mouseX,
            int mouseY
    ) {

        Minecraft minecraft =
                Minecraft.getInstance();

        if (!visible) {
            return;
        }

        Component hoveredTooltip = null;

        updatePosition(screen);

        int width =
                getPanelWidth();

        int panelHeight =
                getPanelHeight();

        int bottom =
                top + panelHeight;


        /*
         * SEARCH BOX
         */

        updateSearchBoxPosition();

        if (searchBox != null) {

            int searchX = searchBox.getX();
            int searchY = searchBox.getY();
            int searchWidth = searchBox.getWidth();
            int searchHeight = searchBox.getHeight();

            /*
             * SearchBox background
             */

            graphics.fill(
                    searchX,
                    searchY,
                    searchX + searchWidth,
                    searchY + searchHeight - 2,
                    getSearchBoxColor()
            );

            /*
             * SearchBox
             */

            searchBox.render(
                    graphics,
                    mouseX,
                    mouseY,
                    0
            );



            /*
             * SEARCH CLEAR BUTTON
             */

            if (!searchBox.getValue().isEmpty()) {

                int clearSize = getSearchClearSize();
                int clearX = getSearchClearX();
                int clearY = getSearchClearY();

                boolean hovered =
                        isMouseOverRectangle(
                                mouseX,
                                mouseY,
                                clearX,
                                clearY,
                                clearSize,
                                clearSize
                        );

                ResourceLocation texture =
                        hovered
                                ? SEARCH_CLEAR_HOVERED
                                : SEARCH_CLEAR;

                graphics.blit(
                        texture,
                        clearX,
                        clearY,
                        clearSize,
                        clearSize,
                        0,
                        0,
                        16,
                        16,
                        16,
                        16
                );
            }



        }

        /*
         * FAVORITES FILTER STAR
         */

        int searchStarSize =
                getSearchStarSize();

        int searchStarX =
                getSearchStarX(
                        width,
                        searchStarSize
                );

        int searchStarY = getSearchStarY();

        boolean searchStarHovered =
                isMouseOverRectangle(
                        mouseX,
                        mouseY,
                        searchStarX,
                        searchStarY,
                        searchStarSize,
                        searchStarSize
                );

        drawStar(
                graphics,
                searchStarX,
                searchStarY,
                searchStarSize,
                favoritesOnly,
                searchStarHovered
        );

        if (searchStarHovered) {
            hoveredTooltip = favoritesOnly
                    ? Component.translatable("gui.ymbettercreativemenu.favorite.toggle_back")
                    : Component.translatable("gui.ymbettercreativemenu.favorite.toggle");
        }

        /*
         * CONTENT / SCROLL
         */

        int contentTop = getSearchRowHeight();

        int availableHeight =
                panelHeight
                        - contentTop
                        - PADDING;

        int contentHeight =
                getContentHeight();

        int maxScroll =
                Math.max(
                        0,
                        contentHeight
                                - availableHeight
                );

        scrollOffset =
                Math.max(
                        0,
                        Math.min(
                                scrollOffset,
                                maxScroll
                        )
                );

        int contentRight =
                left
                        + width
                        - SCROLLBAR_WIDTH
                        - SCROLLBAR_GAP;

        /*
         * SEARCH / CONTENT DIVIDER
         */

        graphics.fill(
                left + BORDER_WIDTH,
                top + contentTop - 1,
                left + width - BORDER_WIDTH,
                top + contentTop,
                getBorderColor()
        );

        graphics.enableScissor(
                left + 3,
                top + 2 + contentTop,
                contentRight - 1,
                bottom - PADDING
        );

        int y =
                getContentStartY()
                        - (int) scrollOffset;

        /*
         * MODS
         */

        for (ModEntry mod : mods) {

            if (!matchesSearch(mod)) {
                continue;
            }

            if (favoritesOnly
                    && !containsFavorite(mod)) {
                continue;
            }

            /*
             * ONE TAB MOD
             */

            if (mod.tabs().size() == 1) {

                CreativeModeTab tab =
                        mod.tabs().get(0);

                int rowHeight =
                        calculateModRowHeight();

                boolean rowHovered =
                        isMouseOverRectangle(
                                mouseX,
                                mouseY,
                                left,
                                y,
                                contentRight - left,
                                rowHeight
                        );

                if (rowHovered) {

                    graphics.fill(
                            left,
                            y,
                            contentRight,
                            y + rowHeight,
                            getRowHoverColor()
                    );
                }

                /*
                 * Icon
                 */

                renderItemScaled(
                        graphics,
                        tab.getIconItem(),
                        left + 2 + SCROLLBAR_WIDTH + SCROLLBAR_GAP,
                        y,
                        rowHeight
                );

                /*
                 * Mod name
                 */
                int starSize = getStarSize(rowHeight);
                int starX = getStarX(starSize);

                drawModName(
                        graphics,
                        mod.displayName(),
                        left + 24 + SCROLLBAR_WIDTH,
                        y + calculateTextVerticalPosition(rowHeight) + 1,
                        starX - PADDING
                );

                /*
                 * Favorite star
                 */

                int starY =
                        getRowStarY(
                                y,
                                rowHeight,
                                false
                        );

                boolean starHovered =
                        isMouseOverRectangle(
                                mouseX,
                                mouseY,
                                starX,
                                starY,
                                starSize,
                                starSize
                        );

                drawStar(
                        graphics,
                        starX,
                        starY,
                        starSize,
                        isFavorite(tab),
                        starHovered
                );

                if (starHovered) {
                    hoveredTooltip =
                            Component.translatable(
                                    isFavorite(tab)
                                            ? "gui.ymbettercreativemenu.favorite.remove"
                                            : "gui.ymbettercreativemenu.favorite.add"
                            );
                }

                y += rowHeight;

                continue;
            }

            /*
             * MULTI TAB MOD HEADER
             */

            int modRowHeight =
                    calculateModRowHeight();


            boolean modHovered =
                    isMouseOverRectangle(
                            mouseX,
                            mouseY,
                            left,
                            y,
                            contentRight - left,
                            modRowHeight
                    );

            if (modHovered) {

                graphics.fill(
                        left,
                        y,
                        contentRight,
                        y + modRowHeight,
                        getRowHoverColor()
                );
            }

            /*
             * Mod icon
             */

            renderItemScaled(
                    graphics,
                    mod.tabs()
                            .get(0)
                            .getIconItem(),
                    left + 2 + SCROLLBAR_WIDTH + SCROLLBAR_GAP,
                    y,
                    modRowHeight
            );

            /*
             * Mod name
             */

            int starSize = getChildStarSize();

            int starX = getChildStarX(starSize);

            drawModName(
                    graphics,
                    mod.displayName(),
                    left + 24 + SCROLLBAR_WIDTH,
                    y + calculateTextVerticalPosition(modRowHeight),
                    starX - PADDING
            );

            int collapseSize = getModStarSize();

            int collapseX =
                    getChildStarX(collapseSize);

            int collapseY =
                    getRowStarY(
                            y,
                            modRowHeight,
                            false
                    );

            boolean collapseHovered =
                    isMouseOverRectangle(
                            mouseX,
                            mouseY,
                            collapseX,
                            collapseY,
                            collapseSize,
                            collapseSize
                    );

            drawCollapseButton(
                    graphics,
                    collapseX,
                    collapseY,
                    collapseSize,
                    isCollapsed(mod),
                    collapseHovered
            );

            if (collapseHovered) {
                hoveredTooltip =
                        Component.translatable(
                                isCollapsed(mod)
                                        ? "gui.ymbettercreativemenu.expand"
                                        : "gui.ymbettercreativemenu.collapse"
                        );
            }

            y += modRowHeight;

            if (isCollapsed(mod)) {
                continue;
            }

            /*
             * CHILD TABS
             */

            for (CreativeModeTab tab :
                    mod.tabs()) {

                if (!matchesSearch(mod, tab)) {
                    continue;
                }

                if (favoritesOnly && !isFavorite(tab)) {
                    continue;
                }

                int rowHeight = calculateRowHeight();

                boolean rowHovered =
                        isMouseOverRectangle(
                                mouseX,
                                mouseY,
                                left,
                                y,
                                contentRight - left,
                                rowHeight
                        );

                if (rowHovered) {

                    graphics.fill(
                            left + 8,
                            y,
                            contentRight,
                            y + rowHeight,
                            getRowHoverColor()
                    );
                }

                /*
                 * Child icon
                 */

                renderItemScaled(
                        graphics,
                        tab.getIconItem(),
                        left + 14 + SCROLLBAR_WIDTH,
                        y,
                        rowHeight
                );

                /*
                 * Child name
                 */

                drawChildName(
                        graphics,
                        tab.getDisplayName(),
                        left + 34 + SCROLLBAR_WIDTH,
                        y + calculateTextVerticalPosition(rowHeight),
                        starX - PADDING
                );
                /*
                 * Child favorite star
                 */


                int starY =
                        getRowStarY(
                                y,
                                rowHeight,
                                true
                        );

                boolean starHovered =
                        isMouseOverRectangle(
                                mouseX,
                                mouseY,
                                starX,
                                starY,
                                starSize,
                                starSize
                        );

                drawStar(
                        graphics,
                        starX,
                        starY,
                        starSize,
                        isFavorite(tab),
                        starHovered
                );

                if (starHovered) {
                    hoveredTooltip =
                            Component.translatable(
                                    isFavorite(tab)
                                            ? "gui.ymbettercreativemenu.favorite.remove"
                                            : "gui.ymbettercreativemenu.favorite.add"
                            );
                }

                y += rowHeight;
            }
        }

        graphics.disableScissor();


        /*
         * SCROLLBAR
         */

        if (maxScroll > 0) {

            renderScrollbar(
                    graphics,
                    contentHeight,
                    maxScroll,
                    contentTop,
                    panelHeight
            );
        }


        /*
         * TOOLTIP
         */

        if (hoveredTooltip != null) {

            graphics.renderTooltip(
                    minecraft.font,
                    hoveredTooltip,
                    mouseX,
                    mouseY
            );
        }
    }



    /*
     * TEXT
     */

    private void drawModName(
            GuiGraphics graphics,
            String text,
            int x,
            int y,
            int textRight
    ) {

        graphics.pose().pushPose();

        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        graphics.pose().scale(
                fontSizeScale,
                fontSizeScale,
                1.0f
        );

        int scaledX =
                (int) (x / fontSizeScale);

        int scaledY =
                (int) (y / fontSizeScale);

        Minecraft minecraft = Minecraft.getInstance();
        var font = minecraft.font;

        int availableWidth =
                (int) ((textRight - x) / fontSizeScale);

        String displayText = text;

        if (availableWidth > 0 && font.width(displayText) > availableWidth) {

            String ellipsis = "...";

            int textWidth =
                    availableWidth - font.width(ellipsis);

            if (textWidth > 0) {
                displayText =
                        font.plainSubstrByWidth(
                                text,
                                textWidth
                        ) + ellipsis;
            } else {
                displayText = "";
            }
        }

        graphics.drawString(
                font,
                Component.literal(displayText),
                scaledX,
                scaledY,
                getFontColor(),
                true
        );

        graphics.pose().popPose();
    }

    private void drawChildName(
            GuiGraphics graphics,
            Component text,
            int x,
            int y,
            int textRight
    ) {

        graphics.pose().pushPose();

        float fontSizeScale =
                (float) getFontSize() / 12.0f;

        graphics.pose().scale(
                fontSizeScale,
                fontSizeScale,
                1.0f
        );

        int scaledX =
                (int) (x / fontSizeScale);

        int scaledY =
                (int) (y / fontSizeScale);

        Minecraft minecraft = Minecraft.getInstance();
        var font = minecraft.font;

        int availableWidth =
                (int) ((textRight - x) / fontSizeScale);

        Component displayText = text;

        if (availableWidth > 0 && font.width(text) > availableWidth) {

            String ellipsis = "...";

            int textWidth =
                    availableWidth - font.width(ellipsis);

            if (textWidth > 0) {

                String originalText =
                        text.getString();

                String shortenedText =
                        font.plainSubstrByWidth(
                                originalText,
                                textWidth
                        );

                displayText =
                        Component.literal(
                                shortenedText + ellipsis
                        );
            } else {
                displayText =
                        Component.empty();
            }
        }

        graphics.drawString(
                font,
                displayText,
                scaledX,
                scaledY,
                getFontColor(),
                true
        );

        graphics.pose().popPose();
    }

    /*
     * FAVORITE STARS
     */

    private void drawStar(
            GuiGraphics graphics,
            int x,
            int y,
            int size,
            boolean favorite,
            boolean hovered
    ) {

        ResourceLocation texture;

        if (favorite) {
            texture = hovered
                    ? STAR_FULL_HOVERED
                    : STAR_FULL;
        } else {
            texture = hovered
                    ? STAR_EMPTY_HOVERED
                    : STAR_EMPTY;
        }

        graphics.blit(
                texture,
                x,
                y,
                size,
                size,
                0,
                0,
                STAR_TEXTURE_SIZE,
                STAR_TEXTURE_SIZE,
                STAR_TEXTURE_SIZE,
                STAR_TEXTURE_SIZE
        );
    }

    /*
     * Collapse Button
     */

    private void drawCollapseButton(
            GuiGraphics graphics,
            int x,
            int y,
            int size,
            boolean collapsed,
            boolean hovered
    ) {

        ResourceLocation texture;

        if (collapsed) {
            texture = hovered
                    ? EXPAND_HOVERED
                    : EXPAND;
        } else {
            texture = hovered
                    ? COLLAPSE_HOVERED
                    : COLLAPSE;
        }

        graphics.blit(
                texture,
                x,
                y,
                size,
                size,
                0,
                0,
                16,
                16,
                16,
                16
        );
    }

    private boolean isCollapsed(
            ModEntry mod
    ) {
        return collapsedMods.contains(
                mod.modId()
        );
    }

    private void toggleCollapsed(
            ModEntry mod
    ) {
        if (!collapsedMods.add(mod.modId())) {
            collapsedMods.remove(mod.modId());
        }

        scrollOffset = Math.min(
                scrollOffset,
                Math.max(
                        0,
                        getContentHeight()
                                - (
                                getPanelHeight()
                                        - getSearchRowHeight()
                        )
                )
        );
    }

    /*
     * SEARCH
     */

    private boolean matchesSearch(
            ModEntry mod
    ) {

        String search =
                searchBox == null
                        ? ""
                        : searchBox
                        .getValue()
                        .trim()
                        .toLowerCase();

        if (search.isEmpty()) {
            return true;
        }

        if (mod.modId()
                .toLowerCase()
                .contains(search)) {

            return true;
        }

        if (mod.displayName()
                .toLowerCase()
                .contains(search)) {

            return true;
        }

        return mod.tabs()
                .stream()
                .anyMatch(
                        tab ->
                                tab.getDisplayName()
                                        .getString()
                                        .toLowerCase()
                                        .contains(search)
                );
    }

    private boolean matchesSearch(
            ModEntry mod,
            CreativeModeTab tab
    ) {

        String search =
                searchBox == null
                        ? ""
                        : searchBox
                        .getValue()
                        .trim()
                        .toLowerCase();

        if (search.isEmpty()) {
            return true;
        }

        return mod.modId()
                .toLowerCase()
                .contains(search)
                || mod.displayName()
                .toLowerCase()
                .contains(search)
                || tab.getDisplayName()
                .getString()
                .toLowerCase()
                .contains(search);
    }

    /*
     * FAVORITE DATA
     */

    private boolean containsFavorite(
            ModEntry mod
    ) {

        return mod.tabs()
                .stream()
                .anyMatch(
                        this::isFavorite
                );
    }

    private boolean isFavorite(
            CreativeModeTab tab
    ) {

        ResourceLocation location =
                getTabLocation(tab);

        return location != null
                && favorites.contains(location);
    }

    private ResourceLocation getTabLocation(
            CreativeModeTab tab
    ) {

        return BuiltInRegistries
                .CREATIVE_MODE_TAB
                .getResourceKey(tab)
                .map(ResourceKey::location)
                .orElse(null);
    }

    private void toggleFavorite(
            CreativeModeTab tab
    ) {

        ResourceLocation location =
                getTabLocation(tab);

        if (location == null) {
            return;
        }

        if (!favorites.add(location)) {
            favorites.remove(location);
        }

        saveFavorites();
    }


    /*
     * MOD LIST
     */

    private void rebuild() {

        mods.clear();

        Map<String, List<CreativeModeTab>> groupedTabs =
                new LinkedHashMap<>();

        for (CreativeModeTab tab :
                BuiltInRegistries.CREATIVE_MODE_TAB) {

            ResourceKey<CreativeModeTab> key =
                    BuiltInRegistries.CREATIVE_MODE_TAB
                            .getResourceKey(tab)
                            .orElse(null);

            if (key == null) {
                continue;
            }

            if (tab.getType()
                    != CreativeModeTab.Type.CATEGORY) {
                continue;
            }

            String modId =
                    key.location().getNamespace();

            groupedTabs
                    .computeIfAbsent(
                            modId,
                            ignored ->
                                    new ArrayList<>()
                    )
                    .add(tab);
        }

        for (Map.Entry<String,
                List<CreativeModeTab>> entry
                : groupedTabs.entrySet()) {

            String modId =
                    entry.getKey();

            List<CreativeModeTab> tabs =
                    entry.getValue();

            tabs.sort(
                    Comparator.comparing(
                            tab ->
                                    tab.getDisplayName()
                                            .getString(),
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

            String displayName =
                    getModDisplayName(modId);

            mods.add(
                    new ModEntry(
                            modId,
                            displayName,
                            tabs
                    )
            );
        }

        mods.sort(
                Comparator.comparing(
                        ModEntry::displayName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );
    }

    private String getModDisplayName(
            String modId
    ) {

        return ModList.get()
                .getModContainerById(modId)
                .map(ModContainer::getModInfo)
                .map(info ->
                        info.getDisplayName()
                )
                .orElse(modId);
    }

    /*
     * MOUSE CLICK
     */

    public boolean mouseClicked(
            CreativeModeInventoryScreen screen,
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!visible) {
            return false;
        }

        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        if (!isInsidePanel(mouseX, mouseY)) {
            return false;
        }

        int width =
                getPanelWidth();

        /*
         * SEARCH CLEAR BUTTON MOUSE
         */

        if (searchBox != null
                && !searchBox.getValue().isEmpty()) {

            int clearSize = getSearchClearSize();

            int clearX = getSearchClearX();

            int clearY = getSearchClearY();

            if (isMouseOverRectangle(
                    mouseX,
                    mouseY,
                    clearX,
                    clearY,
                    clearSize,
                    clearSize
            )) {

                searchBox.setValue("");
                searchBox.setFocused(true);

                return true;
            }
        }

        /*
         * SearchBox itself is handled externally.
         */

        if (searchBox != null
                && searchBox.isMouseOver(
                mouseX,
                mouseY
        )) {
            return false;
        }

        /*
         * FAVORITES FILTER STAR MOUSE
         */

        int searchStarSize =
                getSearchStarSize();

        int searchStarX =
                getSearchStarX(
                        width,
                        searchStarSize
                );

        int searchStarY = getSearchStarY();

        if (isMouseOverRectangle(
                mouseX,
                mouseY,
                searchStarX,
                searchStarY,
                searchStarSize,
                searchStarSize
        )) {

            favoritesOnly = !favoritesOnly;
            scrollOffset = 0;

            return true;
        }

        /*
         * CONTENT AREA
         */
        int contentTop = getContentTop();

        if (mouseY < top + contentTop) {
            return false;
        }

        /*
         * SCROLLBAR
         */

        if (isMouseOverScrollbar(
                mouseX,
                mouseY
        )) {

            int panelHeight =
                    getPanelHeight();

            int contentHeight =
                    getContentHeight();

            int availableHeight =
                    panelHeight
                            - contentTop;

            int maxScroll =
                    Math.max(
                            0,
                            contentHeight
                                    - availableHeight
                    );

            if (maxScroll <= 0) {
                return true;
            }

            int trackTop =
                    top
                            + contentTop
                            + 2;

            int trackBottom =
                    top
                            + panelHeight
                            - PADDING
                            - 2;

            int trackHeight =
                    trackBottom
                            - trackTop;

            int thumbHeight =
                    Math.max(
                            20,
                            (int) (
                                    (double) trackHeight
                                            * availableHeight
                                            / contentHeight
                            )
                    );

            thumbHeight =
                    Math.min(
                            thumbHeight,
                            trackHeight
                    );

            int availableTravel =
                    trackHeight
                            - thumbHeight;

            int thumbY =
                    trackTop
                            + (
                            maxScroll == 0
                                    ? 0
                                    : (int) (
                                    availableTravel
                                            * (
                                            scrollOffset
                                                    / maxScroll
                                    )
                            )
                    );

            if (mouseY >= thumbY
                    && mouseY < thumbY + thumbHeight) {

                draggingScrollbar = true;

                scrollbarDragOffset =
                        mouseY - thumbY;

                return true;
            }
            double newThumbY =
                    mouseY
                            - thumbHeight / 2.0;

            newThumbY =
                    Math.max(
                            trackTop,
                            Math.min(
                                    newThumbY,
                                    trackTop
                                            + availableTravel
                            )
                    );

            double ratio =
                    availableTravel == 0
                            ? 0
                            : (
                            newThumbY
                                    - trackTop
                    ) / availableTravel;

            scrollOffset =
                    ratio * maxScroll;

            return true;
        }


        int contentY =
                (int) (
                        mouseY
                                - getContentStartY()
                                + scrollOffset
                );

        int currentY = 0;

        int contentScreenTop =
                getContentStartY()
                        - (int) scrollOffset;

        /*
         * MODS
         */

        for (ModEntry mod : mods) {

            if (!matchesSearch(mod)) {
                continue;
            }

            if (favoritesOnly
                    && !containsFavorite(mod)) {
                continue;
            }

            /*
             * ONE TAB MOD MOUSE
             */

            if (mod.tabs().size() == 1) {

                CreativeModeTab tab =
                        mod.tabs().get(0);

                int rowHeight =
                        calculateModRowHeight();

                if (contentY >= currentY
                        && contentY < currentY + rowHeight) {

                    int starSize =
                            getModStarSize();

                    int starX =
                            getStarX(starSize);

                    int starY =
                            getRowStarY(
                                    contentScreenTop + currentY,
                                    rowHeight,
                                    false
                            );

                    if (isMouseOverRectangle(
                            mouseX,
                            mouseY,
                            starX,
                            starY,
                            starSize,
                            starSize + STAR_CLICK_PADDING_BOTTOM
                    )) {

                        toggleFavorite(tab);

                        return true;
                    }

                    /*
                     * Select tab
                     */

                    ((CreativeInventoryAccessor)
                            screen)
                            .ymbettercreativemenu$selectTab(
                                    tab
                            );

                    return true;
                }

                currentY += rowHeight;

                continue;
            }

            /*
             * MULTI TAB MOD HEADER
             */

            int modRowHeight =
                    calculateModRowHeight();

            if (contentY >= currentY
                    && contentY < currentY + modRowHeight) {

                int collapseSize = getModStarSize();

                int collapseX =
                        getChildStarX(collapseSize);

                int collapseY =
                        getRowStarY(
                                contentScreenTop + currentY,
                                modRowHeight,
                                false
                        );

                if (isMouseOverRectangle(
                        mouseX,
                        mouseY,
                        collapseX,
                        collapseY,
                        collapseSize,
                        collapseSize + STAR_CLICK_PADDING_BOTTOM
                )) {
                    toggleCollapsed(mod);
                    return true;
                }

                return true;
            }

            currentY += modRowHeight;

            if (isCollapsed(mod)) {
                continue;
            }

            /*
             * CHILD TABS
             */

            for (CreativeModeTab tab :
                    mod.tabs()) {

                if (!matchesSearch(
                        mod,
                        tab
                )) {
                    continue;
                }

                if (favoritesOnly
                        && !isFavorite(tab)) {
                    continue;
                }

                int rowHeight =
                        calculateRowHeight();

                if (contentY >= currentY
                        && contentY < currentY + rowHeight) {

                    int starSize =
                            getChildStarSize();

                    int starX =
                            getChildStarX(starSize);

                    int starY =
                            getRowStarY(
                                    contentScreenTop + currentY,
                                    rowHeight,
                                    true
                            );

                    /*
                     * Star click
                     */

                    if (isMouseOverRectangle(
                            mouseX,
                            mouseY,
                            starX,
                            starY,
                            starSize,
                            starSize + STAR_CLICK_PADDING_BOTTOM
                    )) {

                        toggleFavorite(tab);

                        return true;
                    }

                    /*
                     * Select tab
                     */

                    ((CreativeInventoryAccessor)
                            screen)
                            .ymbettercreativemenu$selectTab(
                                    tab
                            );

                    return true;
                }

                currentY += rowHeight;
            }
        }

        return false;
    }

    /*
     * MOUSE DRAGGED
     */

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!visible) {
            return false;
        }

        if (!draggingScrollbar) {
            return false;
        }

        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        int contentTop = getContentTop();

        int panelHeight =
                getPanelHeight();

        int contentHeight =
                getContentHeight();

        int availableHeight =
                panelHeight
                        - contentTop;

        int maxScroll =
                Math.max(
                        0,
                        contentHeight
                                - availableHeight
                );

        if (maxScroll <= 0) {
            return true;
        }

        int trackTop =
                top
                        + contentTop
                        + 2;

        int trackBottom =
                top
                        + panelHeight
                        - 2;

        int trackHeight =
                trackBottom
                        - trackTop;

        int thumbHeight =
                Math.max(
                        20,
                        (int) (
                                (double) trackHeight
                                        * availableHeight
                                        / contentHeight
                        )
                );

        thumbHeight =
                Math.min(
                        thumbHeight,
                        trackHeight
                );

        int availableTravel =
                trackHeight
                        - thumbHeight;

        double newThumbY =
                mouseY - scrollbarDragOffset;

        newThumbY =
                Math.max(
                        trackTop,
                        Math.min(
                                newThumbY,
                                trackTop
                                        + availableTravel
                        )
                );

        double ratio =
                availableTravel == 0
                        ? 0
                        : (
                        newThumbY
                                - trackTop
                ) / availableTravel;

        scrollOffset =
                ratio * maxScroll;

        return true;
    }

    /*
     * MOUSE RELEASED
     */

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button ==
                InputConstants.MOUSE_BUTTON_LEFT) {

            draggingScrollbar = false;
        }

        return false;
    }

    /*
     * MOUSE SCROLL
     */

    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollY
    ) {
        if (!visible) {
            return false;
        }

        if (!isInsidePanel(
                mouseX,
                mouseY
        )) {
            return false;
        }

        if (searchBox != null
                && searchBox.isFocused()
                && searchBox.isMouseOver(
                mouseX,
                mouseY
        )) {

            return false;
        }

        int contentHeight =
                getContentHeight();

        int contentTop = getContentTop();

        int availableHeight =
                getPanelHeight()
                        - contentTop;

        int maxScroll =
                Math.max(
                        0,
                        contentHeight
                                - availableHeight
                );

        if (maxScroll <= 0) {
            return false;
        }

        scrollOffset -=
                scrollY * 12.0;

        scrollOffset =
                Math.max(
                        0,
                        Math.min(
                                scrollOffset,
                                maxScroll
                        )
                );

        return true;
    }

    /*
     * KEYBOARD
     */

    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        if (searchBox == null) {
            return false;
        }

        if (!searchBox.isFocused()) {
            return false;
        }

        return searchBox.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }

    public boolean charTyped(
            char codePoint,
            int modifiers
    ) {

        if (searchBox == null) {
            return false;
        }

        if (!searchBox.isFocused()) {
            return false;
        }

        return searchBox.charTyped(
                codePoint,
                modifiers
        );
    }

    /*
     * HIT DETECTION
     */

    private boolean isInsidePanel(
            double mouseX,
            double mouseY
    ) {

        return mouseX >= left
                && mouseX < left + getPanelWidth()
                && mouseY >= top
                && mouseY < top + getPanelHeight();
    }

    private boolean isMouseOverScrollbar(
            double mouseX,
            double mouseY
    ) {

        int scrollbarX =
                left
                        + getPanelWidth()
                        - SCROLLBAR_WIDTH
                        - 2;

        int scrollbarRight =
                scrollbarX
                        + SCROLLBAR_WIDTH;

        return mouseX >= scrollbarX
                && mouseX < scrollbarRight
                && mouseY >= top
                && mouseY < top + getPanelHeight();
    }

    private boolean isMouseOverScrollbarThumb(
            double mouseX,
            double mouseY
    ) {

        int contentTop = getContentTop();

        int panelHeight =
                getPanelHeight();

        int contentHeight =
                getContentHeight();

        int availableHeight =
                panelHeight
                        - contentTop;

        int maxScroll =
                Math.max(
                        0,
                        contentHeight
                                - availableHeight
                );

        if (maxScroll <= 0) {
            return false;
        }

        int scrollbarX =
                left
                        + getPanelWidth()
                        - SCROLLBAR_WIDTH
                        - 2;

        int trackTop =
                top
                        + contentTop
                        + 2;

        int trackBottom =
                top
                        + panelHeight
                        - 2;

        int trackHeight =
                trackBottom
                        - trackTop;

        int thumbHeight =
                Math.max(
                        20,
                        (int) (
                                (double) trackHeight
                                        * availableHeight
                                        / contentHeight
                        )
                );

        thumbHeight =
                Math.min(
                        thumbHeight,
                        trackHeight
                );

        int availableTravel =
                trackHeight
                        - thumbHeight;

        int thumbY =
                trackTop
                        + (
                        maxScroll == 0
                                ? 0
                                : (int) (
                                availableTravel
                                        * (
                                        scrollOffset
                                                / maxScroll
                                )
                        )
                );

        return mouseX >= scrollbarX
                && mouseX
                < scrollbarX + SCROLLBAR_WIDTH
                && mouseY >= thumbY
                && mouseY < thumbY + thumbHeight;
    }

     /*SCROLLBAR*/
    private void renderScrollbar(
            GuiGraphics graphics,
            int contentHeight,
            int maxScroll,
            int contentTop,
            int panelHeight
    ) {

        int scrollbarX =
                left
                        + getPanelWidth()
                        - SCROLLBAR_WIDTH
                        - 2;

        int trackTop =
                top
                        + contentTop
                        + 2;

        int trackBottom =
                top
                        + panelHeight
                        - 2;

        int trackHeight =
                trackBottom
                        - trackTop;

        int availableHeight =
                panelHeight
                        - contentTop;

        int thumbHeight =
                Math.max(
                        20,
                        (int) (
                                (double) trackHeight
                                        * availableHeight
                                        / contentHeight
                        )
                );

        thumbHeight =
                Math.min(
                        thumbHeight,
                        trackHeight
                );

        int availableTravel =
                trackHeight
                        - thumbHeight;

        int thumbY =
                trackTop
                        + (
                        maxScroll == 0
                                ? 0
                                : (int) (
                                availableTravel
                                        * (
                                        scrollOffset
                                                / maxScroll
                                )
                        )
                );

        graphics.fill(
                scrollbarX,
                trackTop,
                scrollbarX + SCROLLBAR_WIDTH,
                trackBottom,
                0x55202020
        );

        graphics.fill(
                scrollbarX,
                thumbY,
                scrollbarX + SCROLLBAR_WIDTH,
                thumbY + thumbHeight,
                0xFF888888
        );
    }

/*
    CONTENT HEIGHT
*/
    private int getContentHeight() {

        int height = 2 + PADDING;

        for (ModEntry mod : mods) {

            if (!matchesSearch(mod)) {
                continue;
            }

            if (favoritesOnly
                    && !containsFavorite(mod)) {
                continue;
            }

            if (mod.tabs().size() == 1) {

                height +=
                        calculateModRowHeight();

            } else {

                height +=
                        calculateModRowHeight();

                if (isCollapsed(mod)) {
                    continue;
                }

                for (CreativeModeTab tab : mod.tabs()) {

                    if (!matchesSearch(mod, tab)) {
                        continue;
                    }

                    if (favoritesOnly && !isFavorite(tab)) {
                        continue;
                    }

                    height += calculateRowHeight();

                }
            }
        }

        height += PADDING;

        return height;
    }

    private Path getFavoritesPath() {

        return Minecraft.getInstance()
                .gameDirectory
                .toPath()
                .resolve("config")
                .resolve(
                        "ymbettercreativemenu-favorites.txt"
                );
    }

    private void loadFavorites() {

        Path path =
                getFavoritesPath();

        if (!Files.exists(path)) {
            return;
        }

        try {

            favorites.clear();

            for (String line :
                    Files.readAllLines(path)) {

                String value =
                        line.trim();

                if (value.isEmpty()) {
                    continue;
                }

                ResourceLocation location =
                        ResourceLocation.tryParse(
                                value
                        );

                if (location != null) {
                    favorites.add(location);
                }
            }

        } catch (IOException ignored) {
        }
    }

    private void saveFavorites() {

        Path path =
                getFavoritesPath();

        try {

            Files.createDirectories(
                    path.getParent()
            );

            List<String> lines =
                    favorites
                            .stream()
                            .map(
                                    ResourceLocation::toString
                            )
                            .sorted()
                            .toList();

            Files.write(
                    path,
                    lines
            );

        } catch (IOException ignored) {
        }
    }

    private record ModEntry(
            String modId,
            String displayName,
            List<CreativeModeTab> tabs
    ) {
    }
}