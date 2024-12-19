/*
 * Copyright 2024 TerminalMC
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

package dev.terminalmc.nocapes.gui.screen;

import dev.terminalmc.nocapes.config.Config;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;

import static dev.terminalmc.nocapes.util.Localization.localized;

public class ClothScreenProvider {
    /**
     * Builds and returns a Cloth Config options screen.
     * @param parent the current screen.
     * @return a new options {@link Screen}.
     * @throws NoClassDefFoundError if the Cloth Config API mod is not
     * available.
     */
    static Screen getConfigScreen(Screen parent) {
        Config.Options options = Config.get().options;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(localized("name"))
                .setSavingRunnable(Config::save);
        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory modSettings = builder.getOrCreateCategory(localized("option", "category.cape_render"));

        modSettings.addEntry(eb.startBooleanToggle(
                        localized("option", "hideCape"), options.hideCape)
                .setDefaultValue(Config.Options.hideCapeDefault)
                .setSaveConsumer(val -> options.hideCape = val)
                .build());

        modSettings.addEntry(eb.startBooleanToggle(
                        localized("option", "hideElytra"), options.hideElytra)
                .setDefaultValue(Config.Options.hideElytraDefault)
                .setSaveConsumer(val -> options.hideElytra = val)
                .build());

        for (String url : options.capes.keySet()) {
            modSettings.addEntry(eb.startBooleanToggle(
                    localized("cape", url), options.capes.get(url))
                    .setYesNoTextSupplier((val) -> localized("option", val ? "show" : "hide")
                            .withStyle(val ? ChatFormatting.GREEN : ChatFormatting.RED))
                    .setDefaultValue(false)
                    .setSaveConsumer(val -> options.capes.put(url, val))
                    .build());
        }

        return builder.build();
    }
}
