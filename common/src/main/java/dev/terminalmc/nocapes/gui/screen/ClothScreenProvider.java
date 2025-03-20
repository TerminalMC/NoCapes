/*
 * Copyright 2025 TerminalMC
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
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
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

        modSettings.addEntry(eb.startBooleanToggle(localized("option", "hideEverything"),
                        options.hideEverything)
                .setTooltip(localized("option", "hideEverything.tooltip"))
                .setDefaultValue(Config.Options.hideEverythingDefault)
                .setSaveConsumer(val -> options.hideEverything = val)
                .build());

        SubCategoryBuilder capeGroup = eb.startSubCategory(localized("option", "individualCapes"))
                .setExpanded(!options.hideEverything);

        for (String url : options.capes.keySet()) {
            Config.ShowMode mode = options.capes.get(url);
            capeGroup.add(eb.startIntSlider(localized("cape", url),
                            mode.index, 0, Config.ShowMode.values().length - 1)
                    .setTextGetter((val) -> {
                        Config.ShowMode m = Config.ShowMode.values()[val];
                        return localized("option", "showMode." + m).withStyle(m.format);
                    })
                    .setDefaultValue(0)
                    .setSaveConsumer(val -> options.capes.put(url, Config.ShowMode.values()[val]))
                    .build());
        }

        modSettings.addEntry(capeGroup.build());

        return builder.build();
    }
}
