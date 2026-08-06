package net.shirojr.kitting.compat;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.network.packet.SyncLevelZEntityAttributesS2CPacket;
import net.shirojr.kitting.util.KittingNbtKeys;

import java.util.Set;

public class LevelZCompat implements CompatEntry<LevelZCompat> {
    private NbtCompound levelsNbt;

    public LevelZCompat(NbtCompound levelsNbt) {
        if (!isLoaded()) throw new IllegalStateException(getNotLoadedMessage());
        this.levelsNbt = levelsNbt;
    }

    public LevelZCompat() {
        this(new NbtCompound());
    }


    @Override
    public Mod getCompatMod() {
        return Mod.LEVEL_Z;
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.LEVEL_Z)) {
            this.levelsNbt = nbt.getCompound(KittingNbtKeys.LEVEL_Z);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(KittingNbtKeys.LEVEL_Z, this.levelsNbt);
    }

    @Override
    public void update(PlayerEntity player) {
        if (!(player instanceof PlayerStatsManagerAccess access)) return;
        NbtCompound nbt = new NbtCompound();
        access.getPlayerStatsManager().writeNbt(nbt);
        this.levelsNbt = nbt;
    }

    @Override
    public void apply(PlayerEntity player) {
        if (!(player instanceof PlayerStatsManagerAccess access)) return;
        access.getPlayerStatsManager().readNbt(this.levelsNbt);
        syncDataS2C(player);
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        if (!(player instanceof PlayerStatsManagerAccess access)) return;
        PlayerStatsManager manager = access.getPlayerStatsManager();
        for (Skill skill : Skill.values()) {
            manager.setSkillLevel(skill, 0);
        }
        manager.setLevelProgress(0f);
        manager.setOverallLevel(0);
        manager.setTotalLevelExperience(0);
        manager.setSkillPoints(0);
        syncDataS2C(player);
    }

    @Override
    public LevelZCompat copy() {
        return new LevelZCompat(this.levelsNbt.copy());
    }

    private static void syncDataS2C(PlayerEntity target) {
        if (!(target instanceof ServerPlayerEntity serverPlayer)) return;
        if (!(serverPlayer instanceof PlayerStatsManagerAccess access)) return;
        PlayerStatsManager manager = access.getPlayerStatsManager();
        PlayerStatsServerPacket.writeS2CSkillPacket(manager, serverPlayer);
        PlayerStatsServerPacket.writeS2CXPPacket(manager, serverPlayer);
        PlayerStatsServerPacket.writeS2CStrengthPacket(serverPlayer);
        new SyncLevelZEntityAttributesS2CPacket().send(Set.of(serverPlayer));
    }
}
