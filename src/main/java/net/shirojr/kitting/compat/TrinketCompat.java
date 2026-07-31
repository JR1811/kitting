package net.shirojr.kitting.compat;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.NbtKeys;

import java.util.Map;

public class TrinketCompat implements CompatEntry<TrinketCompat> {
    private NbtCompound trinketsNbt;

    public TrinketCompat(NbtCompound trinketsNbt) {
        if (!isLoaded()) throw new IllegalStateException(getNotLoadedMessage());
        this.trinketsNbt = trinketsNbt;
    }

    public TrinketCompat() {
        this(new NbtCompound());
    }

    @Override
    public TrinketCompat copy() {
        return new TrinketCompat(this.trinketsNbt.copy());
    }

    @Override
    public void update(PlayerEntity player) {
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            NbtCompound nbt = new NbtCompound();
            component.writeToNbt(nbt);
            this.trinketsNbt = nbt;
        });
    }

    @Override
    public void apply(PlayerEntity player) {
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (Map<String, TrinketInventory> group : component.getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    inventory.clear();
                }
            }
            component.readFromNbt(this.trinketsNbt);
        });
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (Map<String, TrinketInventory> group : component.getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    inventory.clear();
                }
            }
        });
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(NbtKeys.TRINKETS)) {
            this.trinketsNbt = nbt.getCompound(NbtKeys.TRINKETS);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(NbtKeys.TRINKETS, this.trinketsNbt);
    }

    @Override
    public Mod getCompatMod() {
        return Mod.TRINKETS;
    }
}
