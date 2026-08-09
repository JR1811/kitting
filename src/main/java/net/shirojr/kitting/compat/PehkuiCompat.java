package net.shirojr.kitting.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.KittingNbtKeys;
import virtuoel.pehkui.api.ScaleType;

import java.util.HashMap;

public class PehkuiCompat implements CompatEntry<PehkuiCompat> {
    public static final HashMap<Identifier, ScaleType> ALL_SCALE_TYPES = new HashMap<>();

    private NbtCompound nbt;

    public PehkuiCompat(NbtCompound nbt) {
        if (!isLoaded()) throw new IllegalStateException(getNotLoadedMessage());
        this.nbt = nbt;
    }

    public PehkuiCompat() {
        this(new NbtCompound());
    }

    @Override
    public Mod getCompatMod() {
        return Mod.PEHKUI;
    }

    @Override
    public void update(PlayerEntity player) {
        this.nbt = new NbtCompound();
        ALL_SCALE_TYPES.forEach((identifier, scaleType) -> {
            NbtCompound entryNbt = new NbtCompound();
            scaleType.getScaleData(player).writeNbt(entryNbt);
            this.nbt.put(identifier.toString(), entryNbt);
        });
    }

    @Override
    public void apply(PlayerEntity player) {
        ALL_SCALE_TYPES.forEach((identifier, scaleType) -> {
            if (!this.nbt.contains(identifier.toString())) return;
            scaleType.getScaleData(player).readNbt(this.nbt.getCompound(identifier.toString()));
        });
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        ALL_SCALE_TYPES.forEach((identifier, scaleType) -> scaleType.getScaleData(player).resetScale());
    }

    @Override
    public PehkuiCompat copy() {
        return new PehkuiCompat(this.nbt.copy());
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.PEHKUI)) {
            this.nbt = nbt.getCompound(KittingNbtKeys.PEHKUI);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(KittingNbtKeys.PEHKUI, this.nbt);
    }
}
