package com.yesmenn.bettercreative.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** {@code getGuiLeft()}/{@code getGuiTop()} only exist on NeoForge; this works on every loader. */
@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Accessor("leftPos")
    int ymbettercreativemenu$getLeftPos();

    @Accessor("topPos")
    int ymbettercreativemenu$getTopPos();
}
