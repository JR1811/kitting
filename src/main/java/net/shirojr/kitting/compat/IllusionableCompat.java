package net.shirojr.kitting.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.KittingNbtKeys;

import java.util.HashSet;
import java.util.UUID;

public class IllusionableCompat implements CompatEntry<IllusionableCompat> {
    private boolean isIllusion;
    private HashSet<UUID> illusionTargets;

    public IllusionableCompat(boolean isIllusion, HashSet<UUID> illusionTargets) {
        this.loadCheck();
        this.isIllusion = isIllusion;
        this.illusionTargets = illusionTargets;
    }

    public IllusionableCompat() {
        this(false, new HashSet<>());
    }

    @Override
    public Mod getCompatMod() {
        return Mod.ILLUSIONABLE;
    }

    @Override
    public void update(PlayerEntity player) {
        IllusionComponent component = IllusionComponent.fromEntity(player);
        if (component == null) return;
        this.isIllusion = component.isIllusion();
        this.illusionTargets = new HashSet<>(component.getTargets());
    }

    @Override
    public void apply(PlayerEntity player) {
        IllusionComponent component = IllusionComponent.fromEntity(player);
        if (component == null) return;
        component.setIllusionState(this.isIllusion, false);
        component.modifyTargets(uuids -> {
            uuids.clear();
            uuids.addAll(this.illusionTargets);
        }, false);
        component.sync();
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        IllusionComponent component = IllusionComponent.fromEntity(player);
        if (component == null) return;
        component.setIllusionState(false, false);
        component.modifyTargets(HashSet::clear, false);
        component.sync();
    }

    @Override
    public IllusionableCompat copy() {
        return new IllusionableCompat(this.isIllusion, new HashSet<>(this.illusionTargets));
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.ILLUSION)) {
            NbtCompound illusionNbt = nbt.getCompound(KittingNbtKeys.ILLUSION);
            this.isIllusion = illusionNbt.getBoolean(KittingNbtKeys.IS_ILLUSION);
            this.illusionTargets.clear();
            NbtList targetsNbt = illusionNbt.getList(KittingNbtKeys.UUIDS, NbtElement.STRING_TYPE);
            for (int i = 0; i < targetsNbt.size(); i++) {
                this.illusionTargets.add(UUID.fromString(targetsNbt.getString(i)));
            }
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        NbtCompound illusionNbt = new NbtCompound();
        illusionNbt.putBoolean(KittingNbtKeys.IS_ILLUSION, this.isIllusion);
        NbtList targetsNbt = new NbtList();
        this.illusionTargets.forEach(uuid -> targetsNbt.add(NbtString.of(uuid.toString())));
        illusionNbt.put(KittingNbtKeys.UUIDS, targetsNbt);
        nbt.put(KittingNbtKeys.ILLUSION, illusionNbt);
    }
}
