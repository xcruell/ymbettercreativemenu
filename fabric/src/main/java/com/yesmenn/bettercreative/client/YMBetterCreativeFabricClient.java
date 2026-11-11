package com.yesmenn.bettercreative.client;

import com.yesmenn.bettercreative.config.FabricConfigFile;
import com.yesmenn.bettercreative.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

/** Fabric client entrypoint. The screen hooks themselves are called from {@code MixinCreativeScreenFabric}. */
public final class YMBetterCreativeFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Platform.init(new Platform.Impl() {
            @Override
            public String modDisplayName(String modId) {
                return FabricLoader.getInstance()
                        .getModContainer(modId)
                        .map(container -> container.getMetadata().getName())
                        .orElse(modId);
            }

            @Override
            public void beforeSelectTab(CreativeModeInventoryScreen screen, CreativeModeTab tab) {
                // Fabric pages the creative tabs: show the page that holds the selected tab.
                FabricCreativeInventoryScreen paged = (FabricCreativeInventoryScreen) screen;
                int page = paged.getPage(tab);
                if (page != paged.getCurrentPage()) {
                    paged.switchToPage(page);
                }
            }
        });

        FabricConfigFile.load();
    }
}
