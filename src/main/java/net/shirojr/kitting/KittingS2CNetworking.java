package net.shirojr.kitting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.network.packet.SyncLevelZEntityAttributesS2CPacket;

@Environment(EnvType.CLIENT)
public class KittingS2CNetworking {
    public static void initialize() {
        if (KittingCompat.isLoaded(Mod.LEVEL_Z)) {
            ClientPlayNetworking.registerGlobalReceiver(
                    SyncLevelZEntityAttributesS2CPacket.TYPE,
                    KittingS2CNetworking::handleLevelZEntityAttributeSync
            );
        }
    }

    private static void handleLevelZEntityAttributeSync(SyncLevelZEntityAttributesS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient.getInstance().execute(() -> {
                    if (KittingCompat.isLoaded(Mod.LEVEL_Z)) {
                        SyncLevelZEntityAttributesS2CPacket.applyEntityAttributes(player);
                    }
                }
        );
    }
}
