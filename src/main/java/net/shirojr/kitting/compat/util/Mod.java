package net.shirojr.kitting.compat.util;

public enum Mod {
    NUMISMATIC_OVERHAUL("numismatic-overhaul"),
    ASHBORNRP("ashbornrp"),
    TRINKETS("trinkets"),
    HIDE_BODY_PARTS("hide-body-parts");

    private final String modId;

    Mod(String modId) {
        this.modId = modId;
    }

    public String getModId() {
        return modId;
    }
}