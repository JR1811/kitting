package net.shirojr.kitting.compat.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.Set;

public interface CompatEntry<T extends CompatEntry<T>> {

    Mod getCompatMod();

    void fromNbt(NbtCompound nbt);

    void toNbt(NbtCompound nbt);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(getCompatMod().getModId());
    }

    default String getNotLoadedMessage() {
        return "Loaded %s compat class without the mod being present".formatted(getCompatMod().getModId());
    }

    T copy();

    void clearLiveData(PlayerEntity player);
}
