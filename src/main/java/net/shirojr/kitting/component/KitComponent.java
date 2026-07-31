package net.shirojr.kitting.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shirojr.kitting.Kitting;
import net.shirojr.kitting.component.data.Kit;
import net.shirojr.kitting.init.KittingComponents;
import net.shirojr.kitting.util.NbtKeys;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class KitComponent implements Component, AutoSyncedComponent {
    public static final Identifier KEY = Kitting.id("kit");

    private final PlayerEntity player;
    private final HashMap<Identifier, Kit> storedKits;

    public KitComponent(PlayerEntity player) {
        this.player = player;
        this.storedKits = new HashMap<>();
    }

    public static KitComponent get(PlayerEntity player) {
        return KittingComponents.KIT.get(player);
    }

    public boolean isRegisteredKit(Identifier identifier) {
        return this.storedKits.containsKey(identifier);
    }

    public List<Identifier> getRegisteredKits() {
        return new ArrayList<>(this.storedKits.keySet());
    }

    public void createKit(Identifier identifier) {
        this.storedKits.put(identifier, new Kit());
        this.sync();
    }

    public void createKit(Identifier identifier, Kit kit) {
        this.storedKits.put(identifier, kit);
        this.sync();
    }

    public void modifyKit(Identifier identifier, Consumer<Kit> kitModifier) {
        Kit kit = this.storedKits.get(identifier);
        if (kit == null) return;
        kitModifier.accept(kit);
        this.sync();
    }

    public void updateKit(Identifier identifier) {
        Kit kit = this.storedKits.get(identifier);
        if (kit == null) return;
        kit.updateAll(this.player);
        this.sync();
    }

    public void removeKit(Identifier identifier) {
        this.storedKits.remove(identifier);
        this.sync();
    }

    public void applyKit(Identifier identifier) {
        Kit entry = this.storedKits.get(identifier);
        if (entry == null) return;
        entry.applyAll(this.player);
    }

    public void clearLiveData(Identifier identifier) {
        Kit clearingKit = this.storedKits.get(identifier);
        if (clearingKit != null) clearingKit.clearLiveData(this.player);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.storedKits.clear();
        if (tag.contains(NbtKeys.STORED_KITS)) {
            NbtList storedKitsNbt = tag.getList(NbtKeys.STORED_KITS, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < storedKitsNbt.size(); i++) {
                NbtCompound entryNbt = storedKitsNbt.getCompound(i);
                String kitKey = entryNbt.getString(NbtKeys.KIT_IDENTIFIER);
                Identifier kitId = Identifier.tryParse(kitKey);
                if (kitId == null) {
                    Kitting.LOGGER.warn("Couldn't read Kit ID: {}", kitKey);
                    continue;
                }
                Kit entryKit = Kit.fromNbt(entryNbt);
                this.storedKits.put(kitId, entryKit);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList storedKitsNbt = new NbtList();
        for (var entry : this.storedKits.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString(NbtKeys.KIT_IDENTIFIER, entry.getKey().toString());
            entry.getValue().toNbt(entryNbt);
            storedKitsNbt.add(entryNbt);
        }
        tag.put(NbtKeys.STORED_KITS, storedKitsNbt);
    }

    @SuppressWarnings("unused")
    public void onRespawn(KitComponent newComponent, boolean lossless, boolean keepInventory, boolean sameCharacter) {
        newComponent.storedKits.putAll(this.storedKits);
    }

    @Nullable
    public KitComponent.ShareError share(PlayerEntity other, Identifier id) {
        if (this.player.equals(other)) return ShareError.SHARED_WITH_SELF;
        if (!this.isRegisteredKit(id)) return ShareError.KIT_NOT_FOUND;
        KitComponent otherComponent = get(other);
        if (otherComponent.isRegisteredKit(id)) return ShareError.KIT_ALREADY_PRESENT;
        otherComponent.createKit(id, this.storedKits.get(id).copy());
        return null;
    }

    public void sync() {
        if (!(this.player instanceof ServerPlayerEntity)) return;
        KittingComponents.KIT.sync(this.player);
    }

    public enum ShareError {
        SHARED_WITH_SELF("Shared Kit with self not possible"),
        KIT_NOT_FOUND("Kit not found on source"),
        KIT_ALREADY_PRESENT("Kit already present on target");

        private final Text message;

        ShareError(String message) {
            this.message = Text.literal(message);
        }

        public Text getMessage(PlayerEntity user, PlayerEntity target) {
            MutableText output = Text.empty();
            output.append("[").append(user.getName()).append(Text.literal(" -> ")).append(target.getName()).append("] ");
            output.append(this.message);
            return output.styled(style -> style.withColor(Formatting.RED));
        }
    }
}
