package wftech.caveoverhaul;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
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
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import wftech.caveoverhaul.carvertypes.OldWorldCarverv12;
import wftech.caveoverhaul.carvertypes.OldWorldCarverv12ReverseNoiseDistribution;

public class Init {


	public static void registerDeferred(IEventBus eventBus) {
		WORLD_CARVERS.register(eventBus);
		WORLD_CARVERS_CFG.register(eventBus);
	}

	public static final DeferredRegister<WorldCarver<?>> WORLD_CARVERS =
            DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, "caveoverhaul");
	
	public static final DeferredRegister<ConfiguredWorldCarver<?>> WORLD_CARVERS_CFG =
            DeferredRegister.create(Registry.CONFIGURED_CARVER_REGISTRY, "caveoverhaul");
	
    public static final RegistryObject<ConfiguredWorldCarver<CaveCarverConfiguration>> CFGD = WORLD_CARVERS_CFG.register("vanilla_cave", () ->
    	WorldCarver.CAVE.configured(
					new CaveCarverConfiguration(
							0.15f * 3.0f, //height difference is up to 3x so I 3x it
							(HeightProvider)UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)), 
							(FloatProvider)UniformFloat.of(0.1f, 0.9f), 
							VerticalAnchor.aboveBottom(8), 
							CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), 
							(FloatProvider)UniformFloat.of(0.7f, 1.4f), 
							(FloatProvider)UniformFloat.of(0.8f, 1.3f), 
							(FloatProvider)UniformFloat.of(-1.0f, -0.4f))));
    
    public static final RegistryObject<ConfiguredWorldCarver<CanyonCarverConfiguration>> CFGD_CANYON_FROM_VANILLA = 
		WORLD_CARVERS_CFG.register("canyons", () -> 
    	WorldCarver.CANYON.configured(
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
    	WorldCarver.CANYON.configured(
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
}
