package com.yesmenn.bettercreative;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import com.yesmenn.bettercreative.config.BetterCreativeConfig;
import net.neoforged.fml.config.ModConfig;

@Mod(YMBetterCreativeMenu.MODID)
public class YMBetterCreativeMenu {
    public static final String MODID = "ymbettercreativemenu";
    private static final Logger LOGGER = LogUtils.getLogger();

    public YMBetterCreativeMenu(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(
                ModConfig.Type.CLIENT,
                BetterCreativeConfig.CLIENT_SPEC
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
