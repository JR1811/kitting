package net.shirojr.kitting;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.kitting.init.KittingEvents;

public class KittingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KittingEvents.registerClient();
    }
}
