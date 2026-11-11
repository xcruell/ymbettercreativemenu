package com.yesmenn.bettercreative.client;

import com.yesmenn.bettercreative.config.ConfigFile;
import com.yesmenn.bettercreative.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;

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
            public Path configDir() {
                return FabricLoader.getInstance().getConfigDir();
            }

            @Override
            public void beforeSelectTab(CreativeModeInventoryScreen screen, CreativeModeTab tab) {
                // Fabric pages the creative tabs and refuses to select a tab that is not on the current page, so
                // switch to the tab's page first. Fabric API for 1.20.1 has no public paging API yet (that came
                // with FabricCreativeInventoryScreen in 1.20.5+), so its impl interfaces are used.
                CreativeGuiExtensions paged = (CreativeGuiExtensions) screen;
                int page = ((FabricItemGroup) tab).getPage();
                // Bounded: next/previous stop at the first/last page.
                for (int i = 0; i < 64 && paged.fabric_currentPage() < page; i++) {
                    paged.fabric_nextPage();
                }
                for (int i = 0; i < 64 && paged.fabric_currentPage() > page; i++) {
                    paged.fabric_previousPage();
                }
            }
        });

        ConfigFile.load();
    }
}
