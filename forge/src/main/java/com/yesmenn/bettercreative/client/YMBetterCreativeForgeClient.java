package com.yesmenn.bettercreative.client;

import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

/** Forwards the Forge screen events to the loader-neutral hooks in {@link YMBetterCreativeClient}. */
public final class YMBetterCreativeForgeClient {

    private YMBetterCreativeForgeClient() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onScreenInit);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onContainerBackground);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onRender);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onMouseButtonPressed);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onMouseButtonReleased);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onMouseDragged);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onMouseScrolled);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onKeyPressed);
        MinecraftForge.EVENT_BUS.addListener(YMBetterCreativeForgeClient::onCharTyped);
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        YMBetterCreativeClient.onScreenInit(event.getScreen(), event::addListener);
    }

    private static void onContainerBackground(ContainerScreenEvent.Render.Background event) {
        YMBetterCreativeClient.onContainerBackground(event.getContainerScreen(), event.getGuiGraphics());
    }

    private static void onRender(ScreenEvent.Render.Post event) {
        YMBetterCreativeClient.onRender(event.getScreen(), event.getGuiGraphics(),
                event.getMouseX(), event.getMouseY());
    }

    private static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (YMBetterCreativeClient.onMouseButtonPressed(event.getScreen(),
                event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        }
    }

    private static void onMouseButtonReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (YMBetterCreativeClient.onMouseButtonReleased(event.getScreen(),
                event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        }
    }

    private static void onMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        if (YMBetterCreativeClient.onMouseDragged(event.getScreen(), event.getMouseX(), event.getMouseY(),
                event.getMouseButton(), event.getDragX(), event.getDragY())) {
            event.setCanceled(true);
        }
    }

    private static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (YMBetterCreativeClient.onMouseScrolled(event.getScreen(),
                event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
            event.setCanceled(true);
        }
    }

    private static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (YMBetterCreativeClient.onKeyPressed(event.getScreen(),
                event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
        }
    }

    private static void onCharTyped(ScreenEvent.CharacterTyped.Pre event) {
        if (YMBetterCreativeClient.onCharTyped(event.getScreen(),
                event.getCodePoint(), event.getModifiers())) {
            event.setCanceled(true);
        }
    }
}
