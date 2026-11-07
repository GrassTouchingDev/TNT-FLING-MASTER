package com.tntflingmaster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TntFlingMaster implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybinds.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FlingEngine.tick(client);
        });
    }
}
