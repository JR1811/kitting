package net.shirojr.kitting.component.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.kitting.component.util.KitEntry;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class Kit {
    private final EnumMap<KitFeature, KitEntry<?>> kitEntries;

    public Kit(EnumMap<KitFeature, KitEntry<?>> kitEntries) {
        this.kitEntries = new EnumMap<>(KitFeature.class);
        this.kitEntries.putAll(kitEntries);
    }

    public Kit(EnumSet<KitFeature> toBeCreated) {
        this.kitEntries = new EnumMap<>(KitFeature.class);
        for (KitFeature entry : KitFeature.values()) {
            if (!toBeCreated.contains(entry) || !entry.isAvailable()) continue;
            this.kitEntries.put(entry, entry.createInstance());
        }
    }

    public Kit() {
        this(KitFeature.getAvailable());
    }

    @SuppressWarnings("unused")
    public void update(PlayerEntity player, KitFeature kitFeature) {
        KitEntry<?> kitEntry = this.kitEntries.get(kitFeature);
        if (kitEntry != null) {
            kitEntry.update(player);
        }
    }

    @SuppressWarnings("unused")
    public void apply(PlayerEntity player, KitFeature kitFeature) {
        KitEntry<?> kitEntry = this.kitEntries.get(kitFeature);
        if (kitEntry != null) {
            kitEntry.apply(player);
        }
    }

    public void updateAll(PlayerEntity player) {
        this.kitEntries.forEach((kitFeature, kitEntry) -> kitEntry.update(player));
    }

    public void applyAll(PlayerEntity player) {
        this.kitEntries.forEach((kitFeature, kitEntry) -> kitEntry.apply(player));
    }

    public Kit copy() {
        EnumMap<KitFeature, KitEntry<?>> copiedEntries = new EnumMap<>(KitFeature.class);
        for (Map.Entry<KitFeature, KitEntry<?>> entry : this.kitEntries.entrySet()) {
            copiedEntries.put(entry.getKey(), entry.getValue().copy());
        }
        return new Kit(copiedEntries);
    }

    public void clearLiveData(PlayerEntity player) {
        this.kitEntries.forEach((mod, compatEntry) -> compatEntry.clearLiveData(player));
    }

    public static Kit fromNbt(NbtCompound nbt) {
        Kit kit = new Kit();
        kit.kitEntries.forEach((kitFeature, kitEntry) -> {
            if (!nbt.contains(kitFeature.getNbtKey())) return;
            kitEntry.fromNbt(nbt);
        });
        return kit;
    }

    public void toNbt(NbtCompound nbt) {
        for (KitFeature feature : KitFeature.values()) {
            KitEntry<?> registeredFeature = this.kitEntries.get(feature);
            if (registeredFeature == null) {
                nbt.remove(feature.getNbtKey());
            } else {
                registeredFeature.toNbt(nbt);
            }
        }
    }
}