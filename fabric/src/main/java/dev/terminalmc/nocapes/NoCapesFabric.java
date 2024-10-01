package dev.terminalmc.nocapes;

import net.fabricmc.api.ClientModInitializer;

public class NoCapesFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Main initialization
        NoCapes.init();
    }
}
