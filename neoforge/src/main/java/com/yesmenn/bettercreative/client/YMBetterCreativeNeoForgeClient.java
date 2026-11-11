package com.yesmenn.bettercreative.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Forwards the NeoForge screen events to the loader-neutral hooks in {@link YMBetterCreativeClient}. */
@Mod(
        value = "ymbettercreativemenu",
        dist = Dist.CLIENT
)
@EventBusSubscriber(
        modid = "ymbettercreativemenu",
        value = Dist.CLIENT
)
public final class YMBetterCreativeNeoForgeClient {

    public YMBetterCreativeNeoForgeClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                ConfigurationScreen::new
        );
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        YMBetterCreativeClient.onScreenInit(event.getScreen(), event::addListener);
    }

    @SubscribeEvent
    public static void onContainerBackground(ContainerScreenEvent.Render.Background event) {
        YMBetterCreativeClient.onContainerBackground(event.getContainerScreen(), event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        YMBetterCreativeClient.onRender(event.getScreen(), event.getGuiGraphics(),
                event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (YMBetterCreativeClient.onMouseButtonPressed(event.getScreen(),
                event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseButtonReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (YMBetterCreativeClient.onMouseButtonReleased(event.getScreen(),
                event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        if (YMBetterCreativeClient.onMouseDragged(event.getScreen(), event.getMouseX(), event.getMouseY(),
                event.getMouseButton(), event.getDragX(), event.getDragY())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (YMBetterCreativeClient.onMouseScrolled(event.getScreen(),
                event.getMouseX(), event.getMouseY(), event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (YMBetterCreativeClient.onKeyPressed(event.getScreen(),
                event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCharTyped(ScreenEvent.CharacterTyped.Pre event) {
        if (YMBetterCreativeClient.onCharTyped(event.getScreen(),
                event.getCodePoint(), event.getModifiers())) {
            event.setCanceled(true);
        }
    }
}
