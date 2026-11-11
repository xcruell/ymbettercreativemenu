package com.yesmenn.bettercreative.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Loader-neutral screen hooks for the creative inventory. The loader modules call these from their own events
 * (NeoForge) or mixins (Fabric). Input hooks return {@code true} if the input was consumed and must not reach
 * the screen.
 */
public final class YMBetterCreativeClient {

    private static final Map<Screen, ModListPanel> PANELS =
            new WeakHashMap<>();

    private static boolean panelVisible = true;

    private YMBetterCreativeClient() {
    }

    public static ModListPanel getPanel(Screen screen) {
        ModListPanel panel = PANELS.computeIfAbsent(
                screen,
                ignored -> new ModListPanel()
        );

        panel.setVisible(panelVisible);

        return panel;
    }

    public static void togglePanelVisibility(Screen screen) {
        ModListPanel panel = getPanel(screen);

        panel.toggleVisible();
        panelVisible = panel.isVisible();
    }

    /*
     * SCREEN INIT
     */

    /** After the screen's init; {@code addListener} adds a widget to the screen's children (not renderables). */
    public static void onScreenInit(
            Screen screen,
            Consumer<GuiEventListener> addListener
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox =
                panel.createSearchBox(creativeScreen);

        addListener.accept(searchBox);
    }

    /*
     * BACKGROUND
     */

    /** Right after the container background was drawn. */
    public static void onContainerBackground(
            Screen screen,
            GuiGraphics graphics
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        if (panel.isVisible()) {
            panel.renderBackground(
                    graphics,
                    creativeScreen
            );
        }
    }

    /*
     * PANEL CONTENT
     */

    /** After the whole screen was rendered. */
    public static void onRender(
            Screen screen,
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        getPanel(screen).render(
                graphics,
                creativeScreen,
                mouseX,
                mouseY
        );
    }

    /*
     * MOUSE PRESSED
     */

    public static boolean onMouseButtonPressed(
            Screen screen,
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return false;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isMouseOver(
                mouseX,
                mouseY
        )) {

            searchBox.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );

            return false;
        }

        if (searchBox != null
                && searchBox.isFocused()) {

            searchBox.setFocused(false);
        }

        return panel.mouseClicked(
                creativeScreen,
                mouseX,
                mouseY,
                button
        );
    }

    /*
     * MOUSE RELEASED
     */

    public static boolean onMouseButtonReleased(
            Screen screen,
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return false;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isFocused()) {

            searchBox.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            );

            return false;
        }

        return panel.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    /*
     * MOUSE DRAGGED
     */

    public static boolean onMouseDragged(
            Screen screen,
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return false;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isFocused()) {

            searchBox.mouseDragged(
                    mouseX,
                    mouseY,
                    button,
                    dragX,
                    dragY
            );

            return false;
        }

        return panel.mouseDragged(
                mouseX,
                mouseY,
                button
        );
    }

    /*
     * MOUSE SCROLL
     */

    public static boolean onMouseScrolled(
            Screen screen,
            double mouseX,
            double mouseY,
            double scrollY
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return false;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isFocused()
                && searchBox.isMouseOver(
                mouseX,
                mouseY
        )) {
            return false;
        }

        return panel.mouseScrolled(
                mouseX,
                mouseY,
                scrollY
        );
    }

    /*
     * KEY PRESSED
     */

    public static boolean onKeyPressed(
            Screen screen,
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return false;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox == null) {
            return false;
        }

        if (searchBox.isFocused()) {

            if (keyCode == GLFW.GLFW_KEY_ENTER
                    || keyCode == GLFW.GLFW_KEY_KP_ENTER
                    || keyCode == GLFW.GLFW_KEY_ESCAPE) {

                searchBox.setFocused(false);

                return true;
            }

            searchBox.keyPressed(
                    keyCode,
                    scanCode,
                    modifiers
            );

            return true;
        }

        return false;
    }

    /*
     * CHARACTER TYPED
     */

    public static boolean onCharTyped(
            Screen screen,
            char codePoint,
            int modifiers
    ) {
        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return false;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox == null
                || !searchBox.isFocused()) {
            return false;
        }

        return searchBox.charTyped(
                codePoint,
                modifiers
        );
    }
}
