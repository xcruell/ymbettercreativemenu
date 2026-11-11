package com.yesmenn.bettercreative.mixin.forge;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/** Forge's creative tab pages (a Forge-added field, hence remap = false). */
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeScreenPagesAccessor {

    @Accessor(value = "pages", remap = false)
    List<CreativeTabsScreenPage> ymbettercreativemenu$getPages();
}
