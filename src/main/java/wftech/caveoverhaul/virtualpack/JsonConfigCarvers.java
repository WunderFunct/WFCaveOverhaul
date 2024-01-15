package wftech.caveoverhaul.virtualpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import wftech.caveoverhaul.CaveOverhaul;

/*
 * Ah fuck
 */

public class JsonConfigCarvers {
	
	public static List<ResourceLocation> RESOURCES_TO_DELETE = new ArrayList<ResourceLocation>();
	public static List<ResourceLocation> RESOURCES_TO_ADD = new ArrayList<ResourceLocation>();
	public static Set<String> USED_NAMES = new HashSet<String>();
	

	public static void createWorldtypeCarvers() {
		int world_type = -2;
		
		//-1 = No carvers, not even vanilla. 0 = 1.12 retrogen, 1 = 1.16, 2 = current version vanilla.
		//Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("caveoverhaul", "canyons")).get().openAsReader();
			
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "canyons"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "canyons_low_y"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "caves_noise_distribution"));
		
		//Re-adding one as it's been removed from the mixin
		//RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver1"));
		//RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver2"));
		//RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver3"));
		
		/*
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver4"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver5"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver6"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_carver7"));
		*/

		/*
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer1_lava1"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer1_lava2"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer2_lava"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer3_lava"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer3_water"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer4_water1"));
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer4_water2"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer5_water"));
		
		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer6_water"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer7_water"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_layer8_water"));

		RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_final_stage"));
		*/
		//RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_final_stage"));
		
		//noise_cave_simplex_boring
		//RESOURCES_TO_ADD.add(new ResourceLocation("caveoverhaul", "noise_cave_simplex_boring"));
		
		
		RESOURCES_TO_DELETE.add(new ResourceLocation("minecraft", "canyons"));
		RESOURCES_TO_DELETE.add(new ResourceLocation("minecraft", "cave_extra_underground"));
		RESOURCES_TO_DELETE.add(new ResourceLocation("minecraft", "cave"));
		
		//Removing the final stage carver as it runs out of order- I add it to the RESOURCES_TO_ADD listings
		//because the BiomeModifier will grab it and put it aside, then I delete it since I don't actually want it
		//to apply at the normal stage :)
		//RESOURCES_TO_DELETE.add(new ResourceLocation("caveoverhaul", "noise_underground_rivers_final_stage"));
			
	}
	
}
