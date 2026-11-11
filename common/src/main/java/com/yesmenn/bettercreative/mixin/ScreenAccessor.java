package com.yesmenn.bettercreative.mixin;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Widget lists of a screen. On 1.20.1 the Forge mixin annotation processor cannot resolve invokers for the generic
 * {@code addWidget}/{@code addRenderableWidget}, so {@link com.yesmenn.bettercreative.client.ScreenWidgets} fills
 * these lists the same way those methods do.
 */
@Mixin(Screen.class)
public interface ScreenAccessor {

    @Accessor("children")
    List<GuiEventListener> ymbettercreativemenu$getChildren();

    // Private in vanilla; Forge makes it public via access transformer, Fabric does not.
    @Accessor("renderables")
    List<Renderable> ymbettercreativemenu$getRenderables();

    @Accessor("narratables")
    List<NarratableEntry> ymbettercreativemenu$getNarratables();
}
