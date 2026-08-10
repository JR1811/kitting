package net.shirojr.kitting.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.KittingNbtKeys;
import org.samo_lego.fabrictailor.casts.TailoredPlayer;

public class FabricTailorCompat implements CompatEntry<FabricTailorCompat> {
    private NbtCompound skinDataNbt;

    public FabricTailorCompat(NbtCompound skinDataNbt) {
        this.loadCheck();
        this.skinDataNbt = skinDataNbt;
    }

    public FabricTailorCompat() {
        this(new NbtCompound());
    }

    @Override
    public Mod getCompatMod() {
        return Mod.FABRIC_TAILOR;
    }

    @Override
    public void update(PlayerEntity player) {
        if (!(player instanceof TailoredPlayer tailoredPlayer)) return;
        String skinValue = tailoredPlayer.getSkinValue();
        if (skinValue == null) return;

        NbtCompound nbt = new NbtCompound();
        nbt.putString("value", skinValue);
        String skinSignature = tailoredPlayer.getSkinSignature();
        if (skinSignature != null) {
            nbt.putString("signature", skinSignature);
        }
        this.skinDataNbt = nbt;
    }

    @Override
    public void apply(PlayerEntity player) {
        if (this.skinDataNbt.isEmpty()) return;
        if (!this.skinDataNbt.contains("value")) return;
        if (!(player instanceof TailoredPlayer tailoredPlayer)) return;

        String skinValue = this.skinDataNbt.getString("value");
        String skinSignature = this.skinDataNbt.contains("signature") ? this.skinDataNbt.getString("signature") : null;
        tailoredPlayer.setSkin(skinValue, skinSignature, true);
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        if (!(player instanceof TailoredPlayer tailoredPlayer)) return;
        tailoredPlayer.clearSkin();
    }

    @Override
    public FabricTailorCompat copy() {
        return new FabricTailorCompat(this.skinDataNbt.copy());
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.FABRIC_TAILOR)) {
            this.skinDataNbt = nbt.getCompound(KittingNbtKeys.FABRIC_TAILOR);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(KittingNbtKeys.FABRIC_TAILOR, this.skinDataNbt);
    }
}
