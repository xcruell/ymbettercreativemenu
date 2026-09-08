package com.yesmenn.bettercreative.mixin;

import com.yesmenn.bettercreative.client.ModListPanel;
import com.yesmenn.bettercreative.client.YMBetterCreativeClient;
import com.yesmenn.bettercreative.client.gui.ListToggleButton;
import com.yesmenn.bettercreative.config.BetterCreativeConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin {

    @Unique
    private ListToggleButton ymbettercreativemenu$toggleButton;

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    private void ymbettercreativemenu$addToggleButton(
            CallbackInfo ci
    ) {

        CreativeModeInventoryScreen screen =
                (CreativeModeInventoryScreen)
                        (Object) this;

        int size =
                BetterCreativeConfig
                        .TOGGLE_BUTTON_SIZE
                        .get();

        int x =
                screen.getGuiLeft()
                        + 24
                        + BetterCreativeConfig
                        .TOGGLE_BUTTON_X
                        .get();

        int y =
                screen.getGuiTop()
                        - 48
                        + BetterCreativeConfig
                        .TOGGLE_BUTTON_Y
                        .get();

        ymbettercreativemenu$toggleButton =
                new ListToggleButton(
                        x,
                        y,
                        size,
                        size,
                        button ->
                                YMBetterCreativeClient
                                        .togglePanelVisibility(
                                                screen
                                        )
                );

        ((ScreenAccessor) screen)
                .ymbettercreativemenu$addRenderableWidget(
                        ymbettercreativemenu$toggleButton
                );
    }
}