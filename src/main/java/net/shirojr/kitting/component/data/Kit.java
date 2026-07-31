package net.shirojr.kitting.component.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.shirojr.kitting.KittingCompat;
import net.shirojr.kitting.compat.AshbornCompat;
import net.shirojr.kitting.compat.NumismaticCompat;
import net.shirojr.kitting.compat.TrinketCompat;
import net.shirojr.kitting.compat.util.CompatEntry;
import net.shirojr.kitting.compat.util.Mod;
import net.shirojr.kitting.util.NbtKeys;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private final List<CompatEntry<?>> compatEntries;

    private NbtList inventoryNbt;
    private NbtList enderChestInventoryNbt;
    @Nullable
    private NumismaticCompat numismaticCompat;
    @Nullable
    private TrinketCompat trinketCompat;
    @Nullable
    private AshbornCompat ashbornCompat;

    public Kit(NbtList inventoryNbt, NbtList enderChestInventoryNbt, @Nullable NumismaticCompat numismaticCompat, @Nullable TrinketCompat trinketCompat, @Nullable AshbornCompat ashbornCompat) {
        this.compatEntries = new ArrayList<>();
        this.inventoryNbt = inventoryNbt;
        this.enderChestInventoryNbt = enderChestInventoryNbt;
        this.numismaticCompat = numismaticCompat;
        this.trinketCompat = trinketCompat;
        this.ashbornCompat = ashbornCompat;
        this.refreshCompatEntriesList();
    }

    public Kit() {
        this(
                new NbtList(), new NbtList(),
                KittingCompat.isLoaded(Mod.NUMISMATIC_OVERHAUL) ? new NumismaticCompat() : null,
                KittingCompat.isLoaded(Mod.TRINKETS) ? new TrinketCompat() : null,
                KittingCompat.isLoaded(Mod.ASHBORNRP) ? new AshbornCompat() : null
        );
    }

    private void refreshCompatEntriesList() {
        this.compatEntries.clear();
        if (this.numismaticCompat != null) this.compatEntries.add(this.numismaticCompat);
        if (this.trinketCompat != null) this.compatEntries.add(this.trinketCompat);
        if (this.ashbornCompat != null) this.compatEntries.add(this.ashbornCompat);
    }

    private void setInventory(NbtList inventoryNbt) {
        this.inventoryNbt = inventoryNbt;
    }

    private void updateInventory(PlayerEntity player) {
        this.inventoryNbt.clear();
        player.getInventory().writeNbt(this.inventoryNbt);
    }

    private void applyInventory(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.readNbt(this.inventoryNbt);
    }

    private void setEnderChestInventory(NbtList enderChestInventoryNbt) {
        this.enderChestInventoryNbt = enderChestInventoryNbt;
    }

    private void updateEnderChestInventory(PlayerEntity player) {
        this.enderChestInventoryNbt.clear();
        EnderChestInventory enderChestInventory = player.getEnderChestInventory();
        this.enderChestInventoryNbt.addAll(enderChestInventory.toNbtList());
    }

    private void applyEnderChestInventory(PlayerEntity player) {
        EnderChestInventory enderChestInventory = player.getEnderChestInventory();
        enderChestInventory.clear();
        enderChestInventory.readNbtList(this.enderChestInventoryNbt);
    }

    private void updateNumismaticCurrency(PlayerEntity player) {
        if (this.numismaticCompat != null) {
            this.numismaticCompat.updateStoredCurrency(player);
        }
    }

    private void applyNumismaticCurrency(PlayerEntity player) {
        if (this.numismaticCompat != null) {
            this.numismaticCompat.applyStoredCurrency(player);
        }
    }

    private void updateTrinkets(PlayerEntity player) {
        if (this.trinketCompat != null) {
            this.trinketCompat.updateStoredTrinkets(player);
        }
    }

    private void applyTrinkets(PlayerEntity player) {
        if (this.trinketCompat != null) {
            this.trinketCompat.applyStoredTrinkets(player);
        }
    }

    private void updateAccessories(PlayerEntity player) {
        if (this.ashbornCompat != null) {
            this.ashbornCompat.updateStoredAccessories(player);
        }
    }

    private void applyAccessories(PlayerEntity player) {
        if (this.ashbornCompat != null) {
            this.ashbornCompat.applyStoredAccessories(player);
        }
    }

    public void updateAll(PlayerEntity player) {
        this.updateInventory(player);
        this.updateEnderChestInventory(player);
        this.updateNumismaticCurrency(player);
        this.updateTrinkets(player);
        this.updateAccessories(player);
    }

    public void applyAll(PlayerEntity player) {
        this.applyInventory(player);
        this.applyEnderChestInventory(player);
        this.applyNumismaticCurrency(player);
        this.applyTrinkets(player);
        this.applyAccessories(player);
    }

    public Kit copy() {
        NbtList inventoryNbtCopy = this.inventoryNbt.copy();
        NbtList enderChestInventoryNbtCopy = this.enderChestInventoryNbt.copy();
        NumismaticCompat numismaticCompatCopy = this.numismaticCompat == null ? null : this.numismaticCompat.copy();
        TrinketCompat trinketsCompatCopy = this.trinketCompat == null ? null : this.trinketCompat.copy();
        AshbornCompat ashbornCompat = this.ashbornCompat == null ? null : this.ashbornCompat.copy();
        return new Kit(inventoryNbtCopy, enderChestInventoryNbtCopy, numismaticCompatCopy, trinketsCompatCopy, ashbornCompat);
    }

    public void clearLiveData(PlayerEntity player) {
        player.getInventory().clear();
        player.getEnderChestInventory().clear();
        this.compatEntries.forEach(compatEntry -> compatEntry.clearLiveData(player));
    }

    public static Kit fromNbt(NbtCompound nbt) {
        Kit kit = new Kit();
        if (nbt.contains(NbtKeys.PLAYER_INVENTORY)) {
            kit.setInventory(nbt.getList(NbtKeys.PLAYER_INVENTORY, NbtElement.COMPOUND_TYPE));
        }
        if (nbt.contains(NbtKeys.ENDER_CHEST_INVENTORY)) {
            kit.setEnderChestInventory(nbt.getList(NbtKeys.ENDER_CHEST_INVENTORY, NbtElement.COMPOUND_TYPE));
        }
        if (nbt.contains(NbtKeys.NUMISMATIC_CURRENCY) && KittingCompat.isLoaded(Mod.NUMISMATIC_OVERHAUL)) {
            kit.numismaticCompat = new NumismaticCompat();
            kit.numismaticCompat.fromNbt(nbt);
        }
        if (nbt.contains(NbtKeys.TRINKETS) && KittingCompat.isLoaded(Mod.TRINKETS)) {
            kit.trinketCompat = new TrinketCompat();
            kit.trinketCompat.fromNbt(nbt);
        }
        if (nbt.contains(NbtKeys.ACCESSORIES) && KittingCompat.isLoaded(Mod.ASHBORNRP)) {
            kit.ashbornCompat = new AshbornCompat();
            kit.ashbornCompat.fromNbt(nbt);
        }
        kit.refreshCompatEntriesList();
        return kit;
    }

    public void toNbt(NbtCompound nbt) {
        nbt.put(NbtKeys.PLAYER_INVENTORY, this.inventoryNbt);
        nbt.put(NbtKeys.ENDER_CHEST_INVENTORY, this.enderChestInventoryNbt);

        if (this.numismaticCompat == null) {
            nbt.remove(NbtKeys.NUMISMATIC_CURRENCY);
        } else {
            this.numismaticCompat.toNbt(nbt);
        }

        if (this.trinketCompat == null) {
            nbt.remove(NbtKeys.TRINKETS);
        } else {
            this.trinketCompat.toNbt(nbt);
        }

        if (this.ashbornCompat == null) {
            nbt.remove(NbtKeys.ACCESSORIES);
        } else {
            this.ashbornCompat.toNbt(nbt);
        }
    }
}