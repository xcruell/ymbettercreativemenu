package com.yesmenn.bettercreative;

import com.mojang.logging.LogUtils;
import com.yesmenn.bettercreative.config.NeoForgeConfigSpec;
import com.yesmenn.bettercreative.platform.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(YMBetterCreativeMenuNeoForge.MODID)
public class YMBetterCreativeMenuNeoForge {
    public static final String MODID = "ymbettercreativemenu";
    private static final Logger LOGGER = LogUtils.getLogger();

    public YMBetterCreativeMenuNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Platform.init(modId -> ModList.get()
                .getModContainerById(modId)
                .map(container -> container.getModInfo().getDisplayName())
                .orElse(modId));

        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(
                ModConfig.Type.CLIENT,
                NeoForgeConfigSpec.build()
        );
    }

    public static void debug(ServerPlayer player, String message) {
        player.sendSystemMessage(
                Component.literal("[DEBUG] " + message)
        );
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Better Creative Menu? YesMenn!");
    }
}
