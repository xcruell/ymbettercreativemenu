package com.yesmenn.bettercreative.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class ScaledSearchBox extends EditBox {

    private final float scale;
    private static final int TEXT_OFFSET_X = 4;
    private static final int TEXT_OFFSET_Y = 0;
    public ScaledSearchBox(
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component message,
            float scale
    ) {
        super(
                font,
                x,
                y,
                width,
                height,
                message
        );

        this.scale = scale;

        setBordered(false);
        setTextColor(ModListPanel.getFontColor());
    }

    @Override
    public void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.pose().pushPose();

        graphics.pose().translate(
                getX(),
                getY(),
                0
        );

        graphics.pose().scale(
                scale,
                scale,
                1.0f
        );

        graphics.pose().translate(
                -getX(),
                -getY(),
                0
        );

        graphics.pose().translate(
                TEXT_OFFSET_X / scale,
                (getHeight() / scale - 9) / 2.0f
                        + TEXT_OFFSET_Y / scale,
                0
        );

        setTextColor(ModListPanel.getFontColor());

        super.renderWidget(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        graphics.pose().popPose();
    }
}