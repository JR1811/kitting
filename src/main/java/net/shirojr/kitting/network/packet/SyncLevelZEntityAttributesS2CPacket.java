package net.shirojr.kitting.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.kitting.Kitting;
import net.shirojr.kitting.KittingCompat;
import net.shirojr.kitting.compat.util.Mod;

import java.util.Collection;
import java.util.Optional;

public record SyncLevelZEntityAttributesS2CPacket() implements FabricPacket {
    static {
        if (!KittingCompat.isLoaded(Mod.LEVEL_Z)) {
            throw new IllegalStateException("Loaded Kitting Networking compat class for LevelZ without LevelZ being present");
        }
    }

    public static final PacketType<SyncLevelZEntityAttributesS2CPacket> TYPE = PacketType.create(
            Kitting.id("sync_levelz_entity_attributes"),
            SyncLevelZEntityAttributesS2CPacket::new
    );

    public SyncLevelZEntityAttributesS2CPacket(PacketByteBuf buf) {
        this();
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    public void send(Collection<ServerPlayerEntity> targets) {
        targets.forEach(serverPlayer -> {
            applyEntityAttributes(serverPlayer);
            ServerPlayNetworking.send(serverPlayer, this);
        });
    }

    public static void applyEntityAttributes(LivingEntity entity) {
        if (!(entity instanceof PlayerStatsManagerAccess access)) return;
        PlayerStatsManager manager = access.getPlayerStatsManager();
        Optional.ofNullable(entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).ifPresent(instance ->
                instance.setBaseValue(ConfigInit.CONFIG.healthBase + manager.getSkillLevel(Skill.HEALTH) * ConfigInit.CONFIG.healthBonus)
        );
        Optional.ofNullable(entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).ifPresent(instance ->
                instance.setBaseValue(ConfigInit.CONFIG.movementBase + manager.getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementBonus)
        );
        Optional.ofNullable(entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).ifPresent(instance ->
                instance.setBaseValue(ConfigInit.CONFIG.attackBase + manager.getSkillLevel(Skill.STRENGTH) * ConfigInit.CONFIG.attackBonus)
        );
        Optional.ofNullable(entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).ifPresent(instance ->
                instance.setBaseValue(ConfigInit.CONFIG.defenseBase + manager.getSkillLevel(Skill.DEFENSE) * ConfigInit.CONFIG.defenseBonus)
        );
        Optional.ofNullable(entity.getAttributeInstance(EntityAttributes.GENERIC_LUCK)).ifPresent(instance ->
                instance.setBaseValue(ConfigInit.CONFIG.luckBase + manager.getSkillLevel(Skill.LUCK) * ConfigInit.CONFIG.luckBonus)
        );
        entity.setHealth(entity.getMaxHealth());
    }
}
