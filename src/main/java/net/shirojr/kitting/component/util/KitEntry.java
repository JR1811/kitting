package net.shirojr.kitting.component.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public interface KitEntry<T extends KitEntry<T>> {
    void update(PlayerEntity player);

    void apply(PlayerEntity player);

    void clearLiveData(PlayerEntity player);

    T copy();

    void fromNbt(NbtCompound nbt);

    void toNbt(NbtCompound nbt);
}
