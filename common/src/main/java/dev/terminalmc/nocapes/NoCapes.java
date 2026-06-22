/*
 * Copyright 2026 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.nocapes;

import dev.terminalmc.nocapes.config.Config;
import dev.terminalmc.nocapes.util.Capes;
import dev.terminalmc.nocapes.util.Logging;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static dev.terminalmc.nocapes.config.Config.options;
import static dev.terminalmc.nocapes.util.Localization.localized;

public class NoCapes {

    public static final String MOD_ID = "nocapes";
    public static final String MOD_NAME = "NoCapes";
    public static final Logger LOG = Logging.getLogger(MOD_ID);
    public static final Component PREFIX = Component.empty()
            .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
            .append(Component.literal(MOD_NAME).withStyle(ChatFormatting.GOLD))
            .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
            .withStyle(ChatFormatting.GRAY);
    public static final List<KeyMapping> KEYBINDS = List.of();

    public static final Map<Identifier, String> RESOURCE_CAPE_CACHE = new HashMap<>();

    private NoCapes() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Client initialization.
     */
    public static void init() {
        Config.getAndSave();
        // Use hardcoded list to maintain ordering
        Map<String, Config.ShowMode> capes = new LinkedHashMap<>();
        for (String id : Capes.CAPES) {
            Config.ShowMode mode = Config.ShowMode.BOTH;
            if (options().capes.containsKey(id)) {
                mode = options().capes.remove(id);
            }
            capes.put(id, mode);
        }
        // Add unknown capes to the end
        capes.putAll(options().capes);
        options().capes = capes;
    }

    /**
     * Client after-tick event listener.
     */
    public static void afterClientTick(Minecraft mc) {
    }

    /**
     * Config save listener.
     */
    public static void onConfigSaved(Config config) {
        // If you are maintaining caches based on config, update them here.
    }

    //
    // Cape filtering
    //

    public static boolean blockCape(Identifier location) {
        if (options().hideEverything)
            return true;
        if (RESOURCE_CAPE_CACHE.containsKey(location)) {
            @Nullable Config.ShowMode mode = Config.get().options.capes.get(
                    RESOURCE_CAPE_CACHE.get(location));
            return mode != null && !mode.showCape();
        }
        return false;
    }

    public static boolean blockElytra(Identifier location) {
        if (options().hideEverything)
            return true;
        if (RESOURCE_CAPE_CACHE.containsKey(location)) {
            @Nullable Config.ShowMode mode = Config.get().options.capes.get(
                    RESOURCE_CAPE_CACHE.get(location));
            return mode != null && !mode.showElytra();
        }
        return false;
    }

    public static void checkInConfig(String capeId, String url) {
        if (!options().capes.containsKey(capeId)) {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(PREFIX.copy().append(
                    localized(
                            "message",
                            "unknownCape",
                            Component.literal(capeId.substring(Math.max(0, capeId.length() - 5)))
                                    .withStyle(ChatFormatting.WHITE)
                    )).withStyle(PREFIX.getStyle()
                    .withHoverEvent(new HoverEvent.ShowText(localized("message", "clickToCopy")))
                    .withClickEvent(new ClickEvent.CopyToClipboard(url))));
            options().capes.put(capeId, Config.ShowMode.BOTH);
            Config.save();
        }
    }
}
