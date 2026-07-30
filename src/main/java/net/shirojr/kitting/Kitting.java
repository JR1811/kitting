package net.shirojr.kitting;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import net.shirojr.kitting.init.KittingEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kitting implements ModInitializer {
	public static final String MOD_ID = "kitting";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		KittingEvents.registerCommon();
		LOGGER.info("You've got to be kitting me...");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
