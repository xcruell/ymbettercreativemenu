package com.yesmenn.bettercreative.mixin;

import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.client.gui.screens.Screen")
public interface ScreenAccessor {

    @Invoker("addRenderableWidget")
    GuiEventListener ymbettercreativemenu$addRenderableWidget(GuiEventListener widget);
}