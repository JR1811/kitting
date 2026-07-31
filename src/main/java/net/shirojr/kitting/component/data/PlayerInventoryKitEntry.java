package net.shirojr.kitting.component.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.shirojr.kitting.component.util.KitEntry;
import net.shirojr.kitting.util.KittingNbtKeys;

public class PlayerInventoryKitEntry implements KitEntry<PlayerInventoryKitEntry> {
    private NbtList inventoryNbt = new NbtList();

    @Override
    public void update(PlayerEntity player) {
        this.inventoryNbt.clear();
        player.getInventory().writeNbt(this.inventoryNbt);
    }

    @Override
    public void apply(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.readNbt(this.inventoryNbt);
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        player.getInventory().clear();
    }

    @Override
    public PlayerInventoryKitEntry copy() {
        PlayerInventoryKitEntry copy = new PlayerInventoryKitEntry();
        copy.inventoryNbt = this.inventoryNbt.copy();
        return copy;
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(KittingNbtKeys.PLAYER_INVENTORY)) {
            this.inventoryNbt = nbt.getList(KittingNbtKeys.PLAYER_INVENTORY, NbtElement.COMPOUND_TYPE);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(KittingNbtKeys.PLAYER_INVENTORY, this.inventoryNbt);
    }
}
