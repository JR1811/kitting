package net.shirojr.kitting.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.KittingNbtKeys;

import java.util.HashSet;

public class HideBodyPartsCompat implements CompatEntry<HideBodyPartsCompat> {
    private NbtCompound bodyPartsNbt;

    public HideBodyPartsCompat(NbtCompound bodyPartsNbt) {
        if (!isLoaded()) throw new IllegalStateException(getNotLoadedMessage());
        this.bodyPartsNbt = bodyPartsNbt;
    }

    public HideBodyPartsCompat() {
        this(new NbtCompound());
    }

    @Override
    public Mod getCompatMod() {
        return Mod.HIDE_BODY_PARTS;
    }

    @Override
    public void update(PlayerEntity player) {
        BodyPartComponent component = BodyPartComponent.fromEntity(player);
        if (component == null) return;
        BodyPart.toNbt(component.getHiddenBodyParts(), this.bodyPartsNbt);
    }

    @Override
    public void apply(PlayerEntity player) {
        BodyPartComponent component = BodyPartComponent.fromEntity(player);
        if (component == null) return;
        component.modifyHiddenBodyParts(bodyParts -> {
            bodyParts.clear();
            bodyParts.addAll(BodyPart.fromNbt(this.bodyPartsNbt));
        }, true);
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        BodyPartComponent component = BodyPartComponent.fromEntity(player);
        if (component == null) return;
        component.modifyHiddenBodyParts(HashSet::clear, true);
    }

    @Override
    public HideBodyPartsCompat copy() {
        return new HideBodyPartsCompat(this.bodyPartsNbt.copy());
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.BODY_PARTS)) {
            this.bodyPartsNbt = nbt.getCompound(KittingNbtKeys.BODY_PARTS);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(KittingNbtKeys.BODY_PARTS, this.bodyPartsNbt);
    }
}
