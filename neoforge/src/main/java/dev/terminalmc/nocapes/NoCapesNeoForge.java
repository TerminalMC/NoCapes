package dev.terminalmc.nocapes;

import dev.terminalmc.nocapes.gui.screen.ConfigScreenProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = NoCapes.MOD_ID, dist = Dist.CLIENT)
public class NoCapesNeoForge {
    public NoCapesNeoForge() {
        // Config screen
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (mc, parent) -> ConfigScreenProvider.getConfigScreen(parent));

        // Main initialization
        NoCapes.init();
    }
}
