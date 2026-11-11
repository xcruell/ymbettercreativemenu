package com.yesmenn.bettercreative.client;

import com.yesmenn.bettercreative.config.BetterCreativeConfig;
import com.yesmenn.bettercreative.config.ConfigFile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Config screen for Fabric (opened from Mod Menu). NeoForge uses its generated {@code ConfigurationScreen} instead.
 * One text field per option; invalid input is shown in red and not saved.
 */
public final class ConfigScreen extends Screen {

    private static final int VALID_COLOR = 0xE0E0E0;
    private static final int INVALID_COLOR = 0xFF5555;

    private final Screen parent;
    private final List<OptionEntry> optionEntries = new ArrayList<>();

    public ConfigScreen(Screen parent) {
        super(Component.translatable("ymbettercreativemenu.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        optionEntries.clear();

        OptionList list = new OptionList(minecraft, width, height, 32, height - 32);
        String section = null;

        for (BetterCreativeConfig.Option<?> option : BetterCreativeConfig.OPTIONS) {
            if (!option.section().equals(section)) {
                section = option.section();
                list.add(new SectionEntry(Component.translatable("ymbettercreativemenu.configuration." + section)));
            }

            OptionEntry entry = new OptionEntry(option);
            optionEntries.add(entry);
            list.add(entry);
        }

        addRenderableWidget(list);

        int buttonY = height - 27;

        addRenderableWidget(Button.builder(Component.translatable("ymbettercreativemenu.config.reset"), button -> {
            for (OptionEntry entry : optionEntries) {
                entry.resetToDefault();
            }
        }).bounds(width / 2 - 155, buttonY, 150, 20).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(width / 2 + 5, buttonY, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 1.20.1: Screen#render does not draw the background itself yet.
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        for (OptionEntry entry : optionEntries) {
            entry.apply();
        }
        ConfigFile.save();
        minecraft.setScreen(parent);
    }

    /*
     * LIST
     */

    private static final class OptionList extends ContainerObjectSelectionList<ConfigEntry> {
        // 1.20.1 list constructor: screen height plus top and bottom edge of the list.
        OptionList(Minecraft minecraft, int width, int height, int top, int bottom) {
            super(minecraft, width, height, top, bottom, 24);
        }

        void add(ConfigEntry entry) {
            addEntry(entry);
        }

        @Override
        public int getRowWidth() {
            return 310;
        }
    }

    private abstract static class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {
    }

    private final class SectionEntry extends ConfigEntry {
        private final Component label;

        SectionEntry(Component label) {
            this.label = label.copy().withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {
            graphics.drawCenteredString(font, label, left + width / 2, top + (height - font.lineHeight) / 2, 0xFFFFFF);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    private final class OptionEntry extends ConfigEntry {
        private final BetterCreativeConfig.Option<?> option;
        private final Component label;
        private final EditBox box;

        OptionEntry(BetterCreativeConfig.Option<?> option) {
            this.option = option;
            this.label = Component.translatable(option.translationKey());

            String range = option instanceof BetterCreativeConfig.IntOption intOption
                    ? intOption.min() + " .. " + intOption.max()
                    : "#RRGGBB";

            box = new EditBox(font, 0, 0, 100, 18, label);
            box.setMaxLength(16);
            box.setValue(String.valueOf(option.get()));
            box.setTooltip(Tooltip.create(Component.literal(option.comment() + "\n")
                    .append(Component.literal(range + "  (default: " + option.defaultValue() + ")")
                            .withStyle(ChatFormatting.GRAY))));
            box.setResponder(text -> box.setTextColor(option.parse(text) != null ? VALID_COLOR : INVALID_COLOR));
        }

        void resetToDefault() {
            box.setValue(String.valueOf(option.defaultValue()));
        }

        void apply() {
            applyTyped(option);
        }

        private <T> void applyTyped(BetterCreativeConfig.Option<T> typed) {
            T value = typed.parse(box.getValue());
            if (value != null) {
                ConfigFile.set(typed, value);
            }
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {
            graphics.drawString(font, label, left, top + (height - font.lineHeight) / 2, 0xFFFFFF);
            box.setX(left + width - box.getWidth());
            box.setY(top + (height - box.getHeight()) / 2);
            box.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(box);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(box);
        }
    }
}
