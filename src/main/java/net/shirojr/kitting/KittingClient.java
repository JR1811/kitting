package net.shirojr.kitting;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.kitting.init.KittingEvents;
import net.shirojr.kitting.network.KittingS2CNetworking;

public class KittingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KittingEvents.registerClient();
        KittingS2CNetworking.initialize();
    }
}
