package net.shirojr.kitting.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.kitting.command.KitCommands;

public class KittingEvents {

    public static void registerCommon() {
        CommandRegistrationCallback.EVENT.register(new KitCommands());
    }

    public static void registerClient() {

    }
}
