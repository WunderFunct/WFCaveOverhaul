package wftech.caveoverhaul.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class LazyLoadingSafetyWrapper {

	public static Minecraft getMinecraft() {
		return Minecraft.getInstance();
	}

	public static Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	public static void sendLocalMessage(Component component) {
		Minecraft.getInstance().player.sendSystemMessage(component);
	}
}
