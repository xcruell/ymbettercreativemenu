package com.yesmenn.bettercreative;

import com.yesmenn.bettercreative.client.ConfigScreen;
import com.yesmenn.bettercreative.client.YMBetterCreativeForgeClient;
import com.yesmenn.bettercreative.config.ConfigFile;
import com.yesmenn.bettercreative.mixin.forge.CreativeScreenPagesAccessor;
import com.yesmenn.bettercreative.platform.Platform;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(YMBetterCreativeMenuForge.MODID)
public class YMBetterCreativeMenuForge {
    public static final String MODID = "ymbettercreativemenu";

    public YMBetterCreativeMenuForge() {
        // Client-only mod; on a dedicated server there is nothing to do.
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        Platform.init(new Platform.Impl() {
            @Override
            public String modDisplayName(String modId) {
                return ModList.get()
                        .getModContainerById(modId)
                        .map(container -> container.getModInfo().getDisplayName())
                        .orElse(modId);
            }

            @Override
            public Path configDir() {
                return FMLPaths.CONFIGDIR.get();
            }

            @Override
            public void beforeSelectTab(CreativeModeInventoryScreen screen, CreativeModeTab tab) {
                // Forge pages the creative tabs: show the page that holds the selected tab.
                for (CreativeTabsScreenPage page : ((CreativeScreenPagesAccessor) screen).ymbettercreativemenu$getPages()) {
                    if (page.getVisibleTabs().contains(tab)) {
                        if (page != screen.getCurrentPage()) {
                            screen.setCurrentPage(page);
                        }
                        return;
                    }
                }
            }
        });

        ConfigFile.load();
        YMBetterCreativeForgeClient.register();

        // Forge 1.20.1 has no generated config screen (NeoForge's ConfigurationScreen), so the shared one is used.
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> new ConfigScreen(parent)));
    }
}
