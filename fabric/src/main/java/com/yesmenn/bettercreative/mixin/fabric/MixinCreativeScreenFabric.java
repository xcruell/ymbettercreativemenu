package com.yesmenn.bettercreative.mixin.fabric;

import com.yesmenn.bettercreative.client.YMBetterCreativeClient;
import com.yesmenn.bettercreative.mixin.ScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric counterpart of the NeoForge screen events: calls the loader-neutral hooks in {@link YMBetterCreativeClient}.
 * The input hooks run at HEAD of the creative screen overrides, so before the screen handles the input, like the
 * NeoForge Pre events.
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class MixinCreativeScreenFabric {

    @Unique
    private Screen ym$self() {
        return (Screen) (Object) this;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void ym$init(CallbackInfo ci) {
        Screen screen = ym$self();
        YMBetterCreativeClient.onScreenInit(screen, ((ScreenAccessor) screen)::ymbettercreativemenu$addWidget);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void ym$renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        YMBetterCreativeClient.onContainerBackground(ym$self(), graphics);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void ym$render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        YMBetterCreativeClient.onRender(ym$self(), graphics, mouseX, mouseY);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void ym$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (YMBetterCreativeClient.onMouseButtonPressed(ym$self(), mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void ym$mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (YMBetterCreativeClient.onMouseButtonReleased(ym$self(), mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void ym$mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
                                 CallbackInfoReturnable<Boolean> cir) {
        if (YMBetterCreativeClient.onMouseDragged(ym$self(), mouseX, mouseY, button, dragX, dragY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void ym$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (YMBetterCreativeClient.onMouseScrolled(ym$self(), mouseX, mouseY, scrollY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void ym$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (YMBetterCreativeClient.onKeyPressed(ym$self(), keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void ym$charTyped(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (YMBetterCreativeClient.onCharTyped(ym$self(), codePoint, modifiers)) {
            cir.setReturnValue(true);
        }
    }
}
