package net.shirojr.kitting.compat.util;

import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.kitting.component.util.KitEntry;

public interface CompatEntry<T extends CompatEntry<T>> extends KitEntry<T> {

    Mod getCompatMod();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(getCompatMod().getModId());
    }

    default void loadCheck() {
        if (!isLoaded()) throw new IllegalStateException(getNotLoadedMessage());
    }

    default String getNotLoadedMessage() {
        return "Loaded %s compat class without the mod being present".formatted(getCompatMod().getModId());
    }
}
