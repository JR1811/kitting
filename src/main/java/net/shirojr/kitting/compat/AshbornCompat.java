package net.shirojr.kitting.compat;

import net.shirojr.kitting.KittingCompat;
import net.shirojr.kitting.compat.util.Mod;

public class AshbornCompat {
    static {
        Mod mod = Mod.NUMISMATIC_OVERHAUL;
        if (!KittingCompat.isLoaded(mod)) {
            throw new IllegalStateException("Loaded %s compat class without the mod being present".formatted(mod.getModId()));
        }
    }

    private AshbornCompat() {
    }


}
