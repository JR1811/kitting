package net.shirojr.kitting;

import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.kitting.compat.util.Mod;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class KittingCompat {
    private KittingCompat() {
    }

    public static boolean isLoaded(Mod mod) {
        return FabricLoader.getInstance().isModLoaded(mod.getModId());
    }
}
