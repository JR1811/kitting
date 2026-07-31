package net.shirojr.kitting.component.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.shirojr.kitting.component.util.KitEntry;
import net.shirojr.kitting.util.NbtKeys;

public class EnderChestInventoryKitEntry implements KitEntry<EnderChestInventoryKitEntry> {
    private NbtList inventoryNbt = new NbtList();

    @Override
    public void update(PlayerEntity player) {
        this.inventoryNbt.clear();
        this.inventoryNbt = player.getEnderChestInventory().toNbtList();
    }

    @Override
    public void apply(PlayerEntity player) {
        EnderChestInventory inventory = player.getEnderChestInventory();
        inventory.clear();
        inventory.readNbtList(this.inventoryNbt);
    }

    @Override
    public void clearLiveData(PlayerEntity player) {
        player.getEnderChestInventory().clear();
    }

    @Override
    public EnderChestInventoryKitEntry copy() {
        EnderChestInventoryKitEntry copy = new EnderChestInventoryKitEntry();
        copy.inventoryNbt = this.inventoryNbt.copy();
        return copy;
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        if (nbt.contains(NbtKeys.ENDER_CHEST_INVENTORY)) {
            this.inventoryNbt = nbt.getList(NbtKeys.ENDER_CHEST_INVENTORY, NbtElement.COMPOUND_TYPE);
        }
    }

    @Override
    public void toNbt(NbtCompound nbt) {
        nbt.put(NbtKeys.ENDER_CHEST_INVENTORY, this.inventoryNbt);
    }
}
