package com.yesmenn.bettercreative.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Builds the NeoForge {@link ModConfigSpec} from {@link BetterCreativeConfig#OPTIONS} and binds every option to
 * its spec value. Sections, keys, comments and limits are unchanged from the NeoForge-only version, so existing
 * {@code ymbettercreativemenu-client.toml} files keep working.
 */
public final class NeoForgeConfigSpec {

    private NeoForgeConfigSpec() {
    }

    public static ModConfigSpec build() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        String section = null;

        for (BetterCreativeConfig.Option<?> option : BetterCreativeConfig.OPTIONS) {
            if (!option.section().equals(section)) {
                if (section != null) {
                    builder.pop();
                }
                section = option.section();
                builder.push(section);
            }

            builder.comment(option.comment()).translation(option.translationKey());

            if (option instanceof BetterCreativeConfig.IntOption intOption) {
                ModConfigSpec.IntValue value = builder.defineInRange(
                        intOption.key(), (int) intOption.defaultValue(), intOption.min(), intOption.max());
                intOption.bind(value::get);
            } else if (option instanceof BetterCreativeConfig.ColorOption colorOption) {
                ModConfigSpec.ConfigValue<String> value = builder.define(
                        colorOption.key(), colorOption.defaultValue(), BetterCreativeConfig::isValidColor);
                colorOption.bind(value::get);
            }
        }

        if (section != null) {
            builder.pop();
        }

        return builder.build();
    }
}
