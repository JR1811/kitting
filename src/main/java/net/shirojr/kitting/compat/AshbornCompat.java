package net.shirojr.kitting.compat;

import io.github.jr1811.ashbornrp.compat.cca.components.AccessoriesComponent;
import io.github.jr1811.ashbornrp.util.AshbornModNbtKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.KittingNbtKeys;

import java.util.HashSet;

public class AshbornCompat implements CompatEntry<AshbornCompat> {
    private NbtCompound accessoriesNbt;

    public AshbornCompat(NbtCompound accessoriesNbt) {
        if (!isLoaded()) throw new IllegalStateException(getNotLoadedMessage());
        this.accessoriesNbt = accessoriesNbt;
    }

    public AshbornCompat() {
        this(new NbtCompound());
    }

    @Override
    public Mod getCompatMod() {
        return Mod.ASHBORNRP;
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.ACCESSORIES)) {
            this.accessoriesNbt = nbt.getCompound(KittingNbtKeys.ACCESSORIES);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(KittingNbtKeys.ACCESSORIES, this.accessoriesNbt);
    }

    @Override
    public AshbornCompat copy() {
        return new AshbornCompat(this.accessoriesNbt.copy());
    }

    @Override
    public void update(PlayerEntity player) {
        AccessoriesComponent component = AccessoriesComponent.fromEntity(player);
        if (component == null) return;
        NbtCompound ashbornCompatNbt = new NbtCompound();
        component.writeToNbt(ashbornCompatNbt);
        this.accessoriesNbt = ashbornCompatNbt.contains(AshbornModNbtKeys.ACCESSORIES)
                ? ashbornCompatNbt.getCompound(AshbornModNbtKeys.ACCESSORIES)
                : new NbtCompound();
    }

    @Override
    public void apply(PlayerEntity player) {
        AccessoriesComponent component = AccessoriesComponent.fromEntity(player);
        if (component == null) return;
        NbtCompound ashbornCompatNbt = new NbtCompound();
        ashbornCompatNbt.put(AshbornModNbtKeys.ACCESSORIES, this.accessoriesNbt);
        component.readFromNbt(ashbornCompatNbt);
        component.sync();
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        AccessoriesComponent component = AccessoriesComponent.fromEntity(player);
        if (component == null) return;
        component.removeAccessories(true, new HashSet<>(component.getAccessories().keySet()));
    }
}
