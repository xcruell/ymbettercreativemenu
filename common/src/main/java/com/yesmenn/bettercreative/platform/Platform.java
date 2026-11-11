package com.yesmenn.bettercreative.platform;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

/**
 * The few things common code needs from the mod loader. The loader module installs its implementation with
 * {@link #init(Impl)} before anything else of the mod runs.
 */
public final class Platform {

    public interface Impl {
        /** Display name of a loaded mod, or {@code modId} if the mod is unknown. */
        String modDisplayName(String modId);

        /**
         * Called right before a creative tab is selected from the panel. Loaders with paged creative tabs switch to
         * the page that contains {@code tab} here, so the tab bar matches the selected tab.
         */
        default void beforeSelectTab(CreativeModeInventoryScreen screen, CreativeModeTab tab) {
        }
    }

    private static Impl impl;

    private Platform() {
    }

    public static void init(Impl implementation) {
        impl = implementation;
    }

    private static Impl impl() {
        if (impl == null) {
            throw new IllegalStateException("Platform not initialised");
        }
        return impl;
    }

    public static String modDisplayName(String modId) {
        return impl().modDisplayName(modId);
    }

    public static void beforeSelectTab(CreativeModeInventoryScreen screen, CreativeModeTab tab) {
        impl().beforeSelectTab(screen, tab);
    }
}
