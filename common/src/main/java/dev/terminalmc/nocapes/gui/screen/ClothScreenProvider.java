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
                .setTitle(localized("screen", "options"))
                .setSavingRunnable(Config::save);
        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory modSettings = builder.getOrCreateCategory(localized("option", "category.cape_render"));

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
