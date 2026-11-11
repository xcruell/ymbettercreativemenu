package com.yesmenn.bettercreative.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ListToggleButton extends Button {

    private static final ResourceLocation NORMAL =
            new ResourceLocation(
                    "ymbettercreativemenu",
                    "textures/gui/list_toggle.png"
            );

    private static final ResourceLocation HOVER =
            new ResourceLocation(
                    "ymbettercreativemenu",
                    "textures/gui/list_toggle_hover.png"
            );

    public ListToggleButton(
            int x,
            int y,
            int width,
            int height,
            OnPress onPress
    ) {
        super(
                x,
                y,
                width,
                height,
                Component.empty(),
                onPress,
                DEFAULT_NARRATION
        );

        this.setTooltip(
                Tooltip.create(
                        Component.translatable(
                                "gui.ymbettercreativemenu.tooltip.toggle_list"
                        )
                )
        );
    }

    @Override
    public void renderWidget(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        ResourceLocation texture =
                this.isHovered()
                        ? HOVER
                        : NORMAL;

        guiGraphics.blit(
                texture,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                0,
                0,
                16,
                16,
                16,
                16
        );
    }
}