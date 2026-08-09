package net.shirojr.kitting.component.data;

import net.shirojr.kitting.KittingCompat;
import net.shirojr.kitting.compat.*;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.component.util.KitEntry;
import net.shirojr.kitting.util.KittingNbtKeys;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Supplier;

public enum KitFeature {
    INVENTORY(null, KittingNbtKeys.PLAYER_INVENTORY, PlayerInventoryKitEntry::new),
    ENDER_CHEST_INVENTORY(null, KittingNbtKeys.ENDER_CHEST_INVENTORY, EnderChestInventoryKitEntry::new),
    NUMISMATIC_OVERHAUL(Mod.NUMISMATIC_OVERHAUL, KittingNbtKeys.NUMISMATIC_CURRENCY, NumismaticCompat::new),
    TRINKETS(Mod.TRINKETS, KittingNbtKeys.TRINKETS, TrinketCompat::new),
    ASHBORNRP(Mod.ASHBORNRP, KittingNbtKeys.ACCESSORIES, AshbornCompat::new),
    HIDE_BODY_PARTS(Mod.HIDE_BODY_PARTS, KittingNbtKeys.BODY_PARTS, HideBodyPartsCompat::new),
    ILLUSIONABLE(Mod.ILLUSIONABLE, KittingNbtKeys.ILLUSION, IllusionableCompat::new),
    LEVEL_Z(Mod.LEVEL_Z, KittingNbtKeys.LEVEL_Z, LevelZCompat::new),
    PEHKUI(Mod.PEHKUI, KittingNbtKeys.PEHKUI, PehkuiCompat::new);

    private final @Nullable Mod requiredMod;
    private final String nbtKey;
    private final Supplier<KitEntry<?>> instanceCreator;

    KitFeature(@Nullable Mod requiredMod, String nbtKey,  Supplier<KitEntry<?>> instanceCreator) {
        this.requiredMod = requiredMod;
        this.nbtKey = nbtKey;
        this.instanceCreator = instanceCreator;
    }

    public KitEntry<?> createInstance() {
        return this.instanceCreator.get();
    }

    public static EnumSet<KitFeature> getAvailable() {
        EnumSet<KitFeature> output = EnumSet.noneOf(KitFeature.class);
        for (KitFeature value : KitFeature.values()) {
            if (value.isAvailable()) output.add(value);
        }
        return output;
    }

    public boolean isAvailable() {
        return this.getRequiredMod() == null || KittingCompat.isLoaded(this.getRequiredMod());
    }

    public @Nullable Mod getRequiredMod() {
        return requiredMod;
    }

    public String getNbtKey() {
        return nbtKey;
    }
}
