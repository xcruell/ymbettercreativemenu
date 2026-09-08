package com.yesmenn.bettercreative.client;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.WeakHashMap;

@Mod(
        value = "ymbettercreativemenu",
        dist = Dist.CLIENT
)
@EventBusSubscriber(
        modid = "ymbettercreativemenu",
        value = Dist.CLIENT
)
public final class YMBetterCreativeClient {

    private static final Map<Screen, ModListPanel> PANELS =
            new WeakHashMap<>();

    private static boolean panelVisible = true;

    public YMBetterCreativeClient(ModContainer modContainer) {

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                ConfigurationScreen::new
        );
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

    @SubscribeEvent
    public static void onScreenInit(
            ScreenEvent.Init.Post event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox =
                panel.createSearchBox(creativeScreen);

        event.addListener(searchBox);
    }

    /*
     * BACKGROUND
     */

    @SubscribeEvent
    public static void onContainerBackground(
            ContainerScreenEvent.Render.Background event
    ) {
        Screen screen = event.getContainerScreen();

        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        if (panel.isVisible()) {
            panel.renderBackground(
                    event.getGuiGraphics(),
                    creativeScreen
            );
        }
    }

    /*
     * PANEL CONTENT
     */

    @SubscribeEvent
    public static void onRender(
            ScreenEvent.Render.Post event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        getPanel(screen).render(
                event.getGuiGraphics(),
                creativeScreen,
                event.getMouseX(),
                event.getMouseY()
        );
    }

    /*
     * MOUSE PRESSED
     */

    @SubscribeEvent
    public static void onMouseButtonPressed(
            ScreenEvent.MouseButtonPressed.Pre event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen creativeScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isMouseOver(
                event.getMouseX(),
                event.getMouseY()
        )) {

            searchBox.mouseClicked(
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getButton()
            );

            return;
        }

        if (searchBox != null
                && searchBox.isFocused()) {

            searchBox.setFocused(false);
        }

        if (panel.mouseClicked(
                creativeScreen,
                event.getMouseX(),
                event.getMouseY(),
                event.getButton()
        )) {
            event.setCanceled(true);
        }
    }

    /*
     * MOUSE RELEASED
     */

    @SubscribeEvent
    public static void onMouseButtonReleased(
            ScreenEvent.MouseButtonReleased.Pre event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isFocused()) {

            searchBox.mouseReleased(
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getButton()
            );

            return;
        }

        if (panel.mouseReleased(
                event.getMouseX(),
                event.getMouseY(),
                event.getButton()
        )) {
            event.setCanceled(true);
        }
    }

    /*
     * MOUSE DRAGGED
     */

    @SubscribeEvent
    public static void onMouseDragged(
            ScreenEvent.MouseDragged.Pre event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isFocused()) {

            searchBox.mouseDragged(
                    event.getMouseX(),
                    event.getMouseY(),
                    event.getMouseButton(),
                    event.getDragX(),
                    event.getDragY()
            );

            return;
        }

        if (panel.mouseDragged(
                event.getMouseX(),
                event.getMouseY(),
                event.getMouseButton()
        )) {
            event.setCanceled(true);
        }
    }

    /*
     * MOUSE SCROLL
     */

    @SubscribeEvent
    public static void onMouseScrolled(
            ScreenEvent.MouseScrolled.Pre event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox != null
                && searchBox.isFocused()
                && searchBox.isMouseOver(
                event.getMouseX(),
                event.getMouseY()
        )) {
            return;
        }

        if (panel.mouseScrolled(
                event.getMouseX(),
                event.getMouseY(),
                event.getScrollDeltaY()
        )) {
            event.setCanceled(true);
        }
    }

    /*
     * KEY PRESSED
     */

    @SubscribeEvent
    public static void onKeyPressed(
            ScreenEvent.KeyPressed.Pre event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox == null) {
            return;
        }

        if (searchBox.isFocused()) {

            if (event.getKeyCode() == GLFW.GLFW_KEY_ENTER
                    || event.getKeyCode() == GLFW.GLFW_KEY_KP_ENTER
                    || event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE) {

                searchBox.setFocused(false);

                event.setCanceled(true);
                return;
            }

            if (searchBox.keyPressed(
                    event.getKeyCode(),
                    event.getScanCode(),
                    event.getModifiers()
            )) {
                event.setCanceled(true);
                return;
            }

            event.setCanceled(true);
        }
    }

    /*
     * CHARACTER TYPED
     */

    @SubscribeEvent
    public static void onCharTyped(
            ScreenEvent.CharacterTyped.Pre event
    ) {
        Screen screen = event.getScreen();

        if (!(screen instanceof CreativeModeInventoryScreen)) {
            return;
        }

        ModListPanel panel = getPanel(screen);

        EditBox searchBox = panel.getSearchBox();

        if (searchBox == null
                || !searchBox.isFocused()) {
            return;
        }

        if (searchBox.charTyped(
                event.getCodePoint(),
                event.getModifiers()
        )) {
            event.setCanceled(true);
        }
    }
}