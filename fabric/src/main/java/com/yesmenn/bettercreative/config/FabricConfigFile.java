package com.yesmenn.bettercreative.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Fabric storage for {@link BetterCreativeConfig}: {@code config/ymbettercreativemenu-client.json}, grouped by the
 * same sections and keys as the NeoForge TOML. Invalid or missing values fall back to the default.
 */
public final class FabricConfigFile {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<BetterCreativeConfig.Option<?>, Object> VALUES = new HashMap<>();

    private FabricConfigFile() {
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("ymbettercreativemenu-client.json");
    }

    public static void load() {
        JsonObject root = new JsonObject();
        Path path = path();
        boolean rewrite = !Files.exists(path);

        if (!rewrite) {
            try {
                root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (IOException | RuntimeException e) {
                LOGGER.warn("Could not read {}, using defaults", path, e);
                rewrite = true;
            }
        }

        VALUES.clear();

        for (BetterCreativeConfig.Option<?> option : BetterCreativeConfig.OPTIONS) {
            Object value = read(root, option);

            if (value == null) {
                value = option.defaultValue();
                rewrite = true;
            }

            VALUES.put(option, value);
            bind(option);
        }

        if (rewrite) {
            save();
        }
    }

    private static Object read(JsonObject root, BetterCreativeConfig.Option<?> option) {
        if (!(root.get(option.section()) instanceof JsonObject section)) {
            return null;
        }

        JsonElement element = section.get(option.key());

        if (element == null || !element.isJsonPrimitive()) {
            return null;
        }

        return option.parse(element.getAsString());
    }

    @SuppressWarnings("unchecked")
    private static <T> void bind(BetterCreativeConfig.Option<T> option) {
        option.bind(() -> (T) VALUES.get(option));
    }

    /** Sets a value in memory; call {@link #save()} afterwards. Invalid values are ignored. */
    public static <T> void set(BetterCreativeConfig.Option<T> option, T value) {
        if (option.isValid(value)) {
            VALUES.put(option, value);
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();

        for (BetterCreativeConfig.Option<?> option : BetterCreativeConfig.OPTIONS) {
            JsonObject section = root.getAsJsonObject(option.section());

            if (section == null) {
                section = new JsonObject();
                root.add(option.section(), section);
            }

            Object value = option.get();

            if (value instanceof Integer i) {
                section.addProperty(option.key(), i);
            } else {
                section.addProperty(option.key(), String.valueOf(value));
            }
        }

        Path path = path();

        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn("Could not write {}", path, e);
        }
    }
}
