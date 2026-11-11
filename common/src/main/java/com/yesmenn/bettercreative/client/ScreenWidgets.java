package com.yesmenn.bettercreative.client;

import com.yesmenn.bettercreative.mixin.ScreenAccessor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

/** Same as the protected {@code Screen#addWidget} / {@code Screen#addRenderableWidget}, callable from outside. */
public final class ScreenWidgets {

    private ScreenWidgets() {
    }

    /** Adds a child without rendering it, like Forge's {@code ScreenEvent.Init#addListener}. */
    public static void addWidget(Screen screen, GuiEventListener widget) {
        ScreenAccessor accessor = (ScreenAccessor) screen;
        accessor.ymbettercreativemenu$getChildren().add(widget);
        if (widget instanceof NarratableEntry narratable) {
            accessor.ymbettercreativemenu$getNarratables().add(narratable);
        }
    }

    public static <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(
            Screen screen, T widget) {
        ((ScreenAccessor) screen).ymbettercreativemenu$getRenderables().add(widget);
        addWidget(screen, widget);
        return widget;
    }
}
