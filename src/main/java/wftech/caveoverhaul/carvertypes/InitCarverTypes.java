package wftech.caveoverhaul.carvertypes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/*
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
 */

import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.carvertypes.rivers.*;

public class InitCarverTypes {

	public static void registerDeferred(IEventBus eventBus) {
		WORLD_CARVERS.register(eventBus);
	}

	public static DeferredRegister<WorldCarver<?>> WORLD_CARVERS =
			DeferredRegister.create(Registries.CARVER, CaveOverhaul.MOD_ID);

	//public final static DeferredRegister<? extends CaveWorldCarver> MYCELIUM_CAVE = 
	//		WORLD_CARVERS.register("mycelium_cave", () -> new OldWorldCarverv12(CaveCarverConfiguration.CODEC));


	public final static DeferredHolder<WorldCarver<?>, BlankCarver> BLANK_CARVER =
			WORLD_CARVERS.register("blank_carver", () -> new BlankCarver(BlankCarver.CODEC));

	public final static DeferredHolder<WorldCarver<?>, OldWorldCarverv12> V12_CAVES =
			WORLD_CARVERS.register("v12_caves", () -> new OldWorldCarverv12(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, OldWorldCarverv16> V16_CAVES =
			WORLD_CARVERS.register("v16_caves", () -> new OldWorldCarverv16(OldWorldCarverv12.CODEC));

	/*
	public final static DeferredRegister<? extends CaveWorldCarver> MYCELIUM_CAVE = 
			WORLD_CARVERS.register("mycelium_cave", () -> new OldWorldCarverv12(CaveCarverConfiguration.CODEC));
	*/
	public final static DeferredHolder<WorldCarver<?>, NoiseCavernTopLayer3> NOISE_CARVER_LAYER_1 =
			WORLD_CARVERS.register("noise_carver_layer_1", () -> new NoiseCavernTopLayer3(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseCavernTopLayer2> NOISE_CARVER_LAYER_3 =
			WORLD_CARVERS.register("noise_carver_layer_2", () -> new NoiseCavernTopLayer2(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseCavernTopLayer1> NOISE_CARVER_LAYER_4 =
			WORLD_CARVERS.register("noise_carver_layer_3", () -> new NoiseCavernTopLayer1(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseCavernMiddleLayer1> NOISE_CARVER_LAYER_5 =
			WORLD_CARVERS.register("noise_carver_layer_4", () -> new NoiseCavernMiddleLayer1(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseCavernMiddleLayer2> NOISE_CARVER_LAYER_6 =
			WORLD_CARVERS.register("noise_carver_layer_5", () -> new NoiseCavernMiddleLayer2(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseCavernBottomLayer1> NOISE_CARVER_LAYER_7 =
			WORLD_CARVERS.register("noise_carver_layer_6", () -> new NoiseCavernBottomLayer1(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseCavernBottomLayer2> NOISE_CARVER_LAYER_8 =
			WORLD_CARVERS.register("noise_carver_layer_7", () -> new NoiseCavernBottomLayer2(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, OldWorldCarverv12ReverseNoiseDistribution> CAVES_NOISE_DISTRIBUTION =
			WORLD_CARVERS.register("caves_noise_distribution", () -> new OldWorldCarverv12ReverseNoiseDistribution(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, VanillaCave> VANILLA_CAVES =
			WORLD_CARVERS.register("vanilla_caves", () -> new VanillaCave(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, VanillaCanyon> VANILLA_CANYON =
			WORLD_CARVERS.register("vanilla_canyon", () -> new VanillaCanyon(BlankCarver.CODEC));


	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver> CAVE_UNDERGROUND_RIVER =
			WORLD_CARVERS.register("noise_underground_rivers", () -> new NoiseUndergroundRiver(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer1_Lava1> CAVE_UNDERGROUND_RIVER_L1_L1 =
			WORLD_CARVERS.register("noise_underground_rivers_layer1_lava1", () -> new NoiseUndergroundRiver_Layer1_Lava1(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer1_Lava2> CAVE_UNDERGROUND_RIVER_L1_L2 =
			WORLD_CARVERS.register("noise_underground_rivers_layer1_lava2", () -> new NoiseUndergroundRiver_Layer1_Lava2(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer2_Lava> CAVE_UNDERGROUND_RIVER_L2_L =
			WORLD_CARVERS.register("noise_underground_rivers_layer2_lava", () -> new NoiseUndergroundRiver_Layer2_Lava(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer3_Lava> CAVE_UNDERGROUND_RIVER_L3_L =
			WORLD_CARVERS.register("noise_underground_rivers_layer3_lava", () -> new NoiseUndergroundRiver_Layer3_Lava(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer3_Water> CAVE_UNDERGROUND_RIVER_L3_W =
			WORLD_CARVERS.register("noise_underground_rivers_layer3_water", () -> new NoiseUndergroundRiver_Layer3_Water(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer4_Water1> CAVE_UNDERGROUND_RIVER_L4_W1 =
			WORLD_CARVERS.register("noise_underground_rivers_layer4_water1", () -> new NoiseUndergroundRiver_Layer4_Water1(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer4_Water2> CAVE_UNDERGROUND_RIVER_L4_W2 =
			WORLD_CARVERS.register("noise_underground_rivers_layer4_water2", () -> new NoiseUndergroundRiver_Layer4_Water2(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer5_Water> CAVE_UNDERGROUND_RIVER_L5_W =
			WORLD_CARVERS.register("noise_underground_rivers_layer5_water", () -> new NoiseUndergroundRiver_Layer5_Water(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer6_Water> CAVE_UNDERGROUND_RIVER_L6_W =
			WORLD_CARVERS.register("noise_underground_rivers_layer6_water", () -> new NoiseUndergroundRiver_Layer6_Water(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer7_Water> CAVE_UNDERGROUND_RIVER_L7_W =
			WORLD_CARVERS.register("noise_underground_rivers_layer7_water", () -> new NoiseUndergroundRiver_Layer7_Water(OldWorldCarverv12.CODEC));

	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiver_Layer8_Water> CAVE_UNDERGROUND_RIVER_L8_W =
			WORLD_CARVERS.register("noise_underground_rivers_layer8_water", () -> new NoiseUndergroundRiver_Layer8_Water(OldWorldCarverv12.CODEC));

	
	
	
	public final static DeferredHolder<WorldCarver<?>, NoiseUndergroundRiverFinalStage> POST_RIVER_DEBUG_TO_STONE =
			WORLD_CARVERS.register("noise_underground_rivers_final_stage", () -> new NoiseUndergroundRiverFinalStage(OldWorldCarverv12.CODEC));

}
