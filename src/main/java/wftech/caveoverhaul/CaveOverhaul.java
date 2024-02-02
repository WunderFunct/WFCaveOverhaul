package wftech.caveoverhaul;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wftech.caveoverhaul.carvertypes.InitCarverTypesFabric;

public class CaveOverhaul implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static String MOD_ID = "caveoverhaul";
	public static String MODID = MOD_ID;
	public static boolean ENABLE_MULTILAYER_RIVERS = false;

    public static final Logger LOGGER = LoggerFactory.getLogger("caveoverhaul");

	@Override
	public void onInitialize() {
		InitCarverTypesFabric.init();
	}


}