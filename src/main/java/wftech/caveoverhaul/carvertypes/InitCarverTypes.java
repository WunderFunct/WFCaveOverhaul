package wftech.caveoverhaul.carvertypes;

import net.minecraft.core.Registry;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.carvertypes.rivers.*;
import wftech.caveoverhaul.carvertypes.simplex.NoiseCaveSimplexBoring;

public class InitCarverTypes {

	public static void registerDeferred(IEventBus eventBus) {
		WORLD_CARVERS.register(eventBus);
		WORLD_CARVERS_CFG.register(eventBus);
	}

	public static final DeferredRegister<WorldCarver<?>> WORLD_CARVERS =
            DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, CaveOverhaul.MOD_ID);
	
	public static final DeferredRegister<ConfiguredWorldCarver<?>> WORLD_CARVERS_CFG =
            DeferredRegister.create(Registry.CONFIGURED_CARVER_REGISTRY, "caveoverhaul");

	//public final static RegistryObject<? extends CaveWorldCarver> MYCELIUM_CAVE = 
	//		WORLD_CARVERS.register("mycelium_cave", () -> new OldWorldCarverv12(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CanyonWorldCarver> BLANK_CARVER = 
			WORLD_CARVERS.register("blank_carver", () -> new BlankCarver(CanyonCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> V12_CAVES = 
			WORLD_CARVERS.register("v12_caves", () -> new OldWorldCarverv12(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> V16_CAVES = 
			WORLD_CARVERS.register("v16_caves", () -> new OldWorldCarverv16(CaveCarverConfiguration.CODEC));

	/*
	public final static RegistryObject<? extends CaveWorldCarver> MYCELIUM_CAVE = 
			WORLD_CARVERS.register("mycelium_cave", () -> new OldWorldCarverv12(CaveCarverConfiguration.CODEC));
	*/
	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_1 = 
			WORLD_CARVERS.register("noise_carver_layer_1", () -> new NoiseCavernTopLayer3(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_3 = 
			WORLD_CARVERS.register("noise_carver_layer_2", () -> new NoiseCavernTopLayer2(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_4 = 
			WORLD_CARVERS.register("noise_carver_layer_3", () -> new NoiseCavernTopLayer1(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_5 = 
			WORLD_CARVERS.register("noise_carver_layer_4", () -> new NoiseCavernMiddleLayer1(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_6 = 
			WORLD_CARVERS.register("noise_carver_layer_5", () -> new NoiseCavernMiddleLayer2(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_7 = 
			WORLD_CARVERS.register("noise_carver_layer_6", () -> new NoiseCavernBottomLayer1(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> NOISE_CARVER_LAYER_8 = 
			WORLD_CARVERS.register("noise_carver_layer_7", () -> new NoiseCavernBottomLayer2(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVES_NOISE_DISTRIBUTION = 
			WORLD_CARVERS.register("caves_noise_distribution", () -> new OldWorldCarverv12ReverseNoiseDistribution(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> VANILLA_CAVES = 
			WORLD_CARVERS.register("vanilla_caves", () -> new VanillaCave(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CanyonWorldCarver> VANILLA_CANYON = 
			WORLD_CARVERS.register("vanilla_canyon", () -> new VanillaCanyon(CanyonCarverConfiguration.CODEC));
	
	//Test

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_NOISE_SIMPLEX_BORING = 
			WORLD_CARVERS.register("noise_cave_simplex_boring", () -> new NoiseCaveSimplexBoring(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER = 
			WORLD_CARVERS.register("noise_underground_rivers", () -> new NoiseUndergroundRiver(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L1_L1 = 
			WORLD_CARVERS.register("noise_underground_rivers_layer1_lava1", () -> new NoiseUndergroundRiver_Layer1_Lava1(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L1_L2 = 
			WORLD_CARVERS.register("noise_underground_rivers_layer1_lava2", () -> new NoiseUndergroundRiver_Layer1_Lava2(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L2_L = 
			WORLD_CARVERS.register("noise_underground_rivers_layer2_lava", () -> new NoiseUndergroundRiver_Layer2_Lava(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L3_L = 
			WORLD_CARVERS.register("noise_underground_rivers_layer3_lava", () -> new NoiseUndergroundRiver_Layer3_Lava(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L3_W = 
			WORLD_CARVERS.register("noise_underground_rivers_layer3_water", () -> new NoiseUndergroundRiver_Layer3_Water(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L4_W1 = 
			WORLD_CARVERS.register("noise_underground_rivers_layer4_water1", () -> new NoiseUndergroundRiver_Layer4_Water1(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L4_W2 = 
			WORLD_CARVERS.register("noise_underground_rivers_layer4_water2", () -> new NoiseUndergroundRiver_Layer4_Water2(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L5_W = 
			WORLD_CARVERS.register("noise_underground_rivers_layer5_water", () -> new NoiseUndergroundRiver_Layer5_Water(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L6_W = 
			WORLD_CARVERS.register("noise_underground_rivers_layer6_water", () -> new NoiseUndergroundRiver_Layer6_Water(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L7_W = 
			WORLD_CARVERS.register("noise_underground_rivers_layer7_water", () -> new NoiseUndergroundRiver_Layer7_Water(CaveCarverConfiguration.CODEC));

	public final static RegistryObject<? extends CaveWorldCarver> CAVE_UNDERGROUND_RIVER_L8_W = 
			WORLD_CARVERS.register("noise_underground_rivers_layer8_water", () -> new NoiseUndergroundRiver_Layer8_Water(CaveCarverConfiguration.CODEC));

	
	
	
	public final static RegistryObject<? extends CaveWorldCarver> POST_RIVER_DEBUG_TO_STONE = 
			WORLD_CARVERS.register("noise_underground_rivers_final_stage", () -> new NoiseUndergroundRiverFinalStage(CaveCarverConfiguration.CODEC));




	public final static RegistryObject<? extends CaveWorldCarver> NEW_MYCELIUM_CAVE_12 = WORLD_CARVERS.register("distributioncave12", () -> new OldWorldCarverv12ReverseNoiseDistribution(CaveCarverConfiguration.CODEC));

    public static final RegistryObject<ConfiguredWorldCarver<CaveCarverConfiguration>> MYCELIUM_CAVES_12 = WORLD_CARVERS_CFG.register("v12_caves_noise", () ->
    	NEW_MYCELIUM_CAVE_12.get().configured(
					new CaveCarverConfiguration(
							1.0f, 
							//+8 modifiers to lift the mycelium tunnels out of lava, too much lava exposure atm
							(HeightProvider)UniformHeight.of(VerticalAnchor.aboveBottom(8+8), VerticalAnchor.aboveBottom(48+8)), 
							(FloatProvider)UniformFloat.of(0.1f, 0.9f), 
							VerticalAnchor.aboveBottom(8), 
							CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), 
							(FloatProvider)UniformFloat.of(0.7f, 1.4f), 
							(FloatProvider)UniformFloat.of(0.8f, 1.3f), 
							(FloatProvider)UniformFloat.of(-1.0f, -0.4f))));
    
    public static final RegistryObject<ConfiguredWorldCarver<CanyonCarverConfiguration>> CFGD_CANYON_FROM_VANILLA = 
		WORLD_CARVERS_CFG.register("canyons", () -> 
		VANILLA_CANYON.get().configured(
    			new CanyonCarverConfiguration(
    					//was 0.02f * 3f * 1f, reducing it as 1.12 caves (new default) are way more dense.
    					0.02f * 3f, //1.18.5 default = 0.01f, 1.16.5 default = 0.02f, height difference is up to 3x so I 3x it. The remaining f is a fudge factor to account
    					//for weirdness
    					(HeightProvider)UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
    					(FloatProvider)UniformFloat.of(0.1f, 0.9f), //ConstantFloat.of(3.0f)
    					VerticalAnchor.aboveBottom(8), 
    					CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), 
    					UniformFloat.of(-0.125f, 0.125f), 
    					new CanyonCarverConfiguration.CanyonShapeConfiguration(
    							UniformFloat.of(0.75f, 1.0f), //distance, think about increasing it
    							TrapezoidFloat.of(0.0f, 6.0f, 2.0f), 
    							3, 
    							UniformFloat.of(0.75f, 1.0f), 
    							1.0f * 4.0f, 0.0f))));	//vertical factor, and vertical center, increasing first by 5x factor
    
    public static final RegistryObject<ConfiguredWorldCarver<CanyonCarverConfiguration>> CFGD_CANYON_FROM_VANILLA_LOW_Y = 
		WORLD_CARVERS_CFG.register("canyons_low_y", () -> 
		VANILLA_CANYON.get().configured(
    			new CanyonCarverConfiguration(
    					//was 0.02f * 3f * 2f, reducing it as 1.12 caves (new default) are way more dense.
    					0.02f * 2f, //1.18.5 default = 0.01f, 1.16.5 default = 0.02f, height difference is up to 3x so I 3x it. The remaining f is a fudge factor to account
    					//for weirdness
    					(HeightProvider)UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(30)),
    					(FloatProvider)UniformFloat.of(0.1f, 0.9f), //ConstantFloat.of(3.0f)
    					VerticalAnchor.aboveBottom(8), 
    					CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), 
    					UniformFloat.of(-0.125f, 0.125f), 
    					new CanyonCarverConfiguration.CanyonShapeConfiguration(
    							UniformFloat.of(0.75f, 1.0f), //distance, think about increasing it
    							TrapezoidFloat.of(0.0f, 6.0f, 2.0f), 
    							3, 
    							UniformFloat.of(0.75f, 1.0f), 
    							1.0f * 4.0f, 0.0f))));	//vertical factor, and vertical center, increasing first by 5x factor

}
