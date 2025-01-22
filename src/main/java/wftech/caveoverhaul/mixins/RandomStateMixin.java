package wftech.caveoverhaul.mixins;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.*;
import net.neoforged.fml.ModList;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderGetter.Provider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.NoiseMaker;
import wftech.caveoverhaul.WorldGenUtils;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.utils.LazyLoadingSafetyWrapper;

import java.util.Map;

@Mixin(RandomState.class)
public class RandomStateMixin {

	@ModifyVariable(method = "<init>(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)V",
			at = @At("HEAD"),
			remap=true)
	private static NoiseGeneratorSettings changeSettingsToOverworld(NoiseGeneratorSettings defaultSettings,
																	NoiseGeneratorSettings noiseGeneratorSettingsIn, HolderGetter<NormalNoise.NoiseParameters> holderIn, final long longIn) {

		//Apparently this runs before we even run the modloader...? Blech, mixins!
		Config.initConfig();

		boolean hasWR = ModList.get().getModContainerById("worldgenrevisited").isPresent();
		if(hasWR) {
			return defaultSettings;
		}

		HolderGetter noise = null;
		HolderGetter density_function = null;
		RegistryAccess registries;
		if(EffectiveSide.get().isClient()) {
			Level level = LazyLoadingSafetyWrapper.getClientLevel();
			registries = level.registryAccess();

			noise = level.holderLookup(Registries.NOISE);
			density_function = level.holderLookup(Registries.DENSITY_FUNCTION);

		} else {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			registries = server.registryAccess();
			//Provider provider = registries.asGetterLookup();
			noise = registries.lookupOrThrow(Registries.NOISE);
			density_function = registries.lookupOrThrow(Registries.DENSITY_FUNCTION);
		}

		Registry<NoiseGeneratorSettings> noiseReg = registries.lookupOrThrow(Registries.NOISE_SETTINGS);

		for(Map.Entry<ResourceKey<NoiseGeneratorSettings>, NoiseGeneratorSettings> entry: noiseReg.entrySet()){
			//CaveOverhaul.LOGGER.error(entry.getKey().location().toString());
			if (entry.getKey().location().getPath().equals("overworld")){
				WorldGenUtils.addNGS(entry.getValue());
			}
		}

		NoiseGeneratorSettings overworldNoise = noiseReg.getOrThrow(NoiseGeneratorSettings.OVERWORLD).value();

		//Store our target settings for later confirmation that we're on the overworld level
		WorldGenUtils.addNGS(overworldNoise);

		for(ResourceLocation key: noiseReg.keySet()) {
			NoiseGeneratorSettings ngs_noise = noiseReg.get(key).get().value();
			if( (!Config.getBoolSetting(Config.KEY_GENERATE_CAVERNS)) & key.getPath().toLowerCase().contains("overworld") && (defaultSettings == ngs_noise)) {

				NoiseRouter defaultNoiseRouter = defaultSettings.noiseRouter();

				HolderGetter hg_noise = noise;
				HolderGetter hg_density_function = density_function;

				DensityFunction densityfunction8 = NoiseRouterDataAccessor.getFunction(hg_density_function, false ? NoiseRouterDataAccessor.FACTOR_LARGE() : (false ? NoiseRouterDataAccessor.FACTOR_AMPLIFIED() : NoiseRouterDataAccessor.FACTOR()));
				DensityFunction densityfunction9 = NoiseRouterDataAccessor.getFunction(hg_density_function, false ? NoiseRouterDataAccessor.DEPTH_LARGE() : (false ? NoiseRouterDataAccessor.DEPTH_AMPLIFIED() : NoiseRouterDataAccessor.DEPTH()));
				DensityFunction densityfunction10 = NoiseRouterDataAccessor.noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
				DensityFunction newTerrainDF = NoiseRouterDataAccessor.slideOverworld(
						false,
						DensityFunctions.add(
								densityfunction10,
								DensityFunctions.constant(-0.703125D)
						).clamp(-64.0D, 64.0D));

				DensityFunction newFinalDensity = NoiseRouterDataAccessor.postProcess(NoiseRouterDataAccessor.getFunction(hg_density_function, NoiseRouterDataAccessor.SLOPED_CHEESE()));

				DensityFunction modifiedDF = NoiseMaker.makeNoise(defaultSettings.noiseRouter().finalDensity());

				NoiseRouter newNoiseRouter = new NoiseRouter(
						defaultNoiseRouter.barrierNoise(),
						defaultNoiseRouter.fluidLevelFloodednessNoise(),
						defaultNoiseRouter.fluidLevelSpreadNoise(),
						defaultNoiseRouter.lavaNoise(),
						defaultNoiseRouter.temperature(),
						defaultNoiseRouter.vegetation(),
						defaultNoiseRouter.continents(),
						defaultNoiseRouter.erosion(),
						defaultNoiseRouter.depth(),
						defaultNoiseRouter.ridges(),
						defaultNoiseRouter.initialDensityWithoutJaggedness(),
						modifiedDF, //newFinalDensity
						defaultNoiseRouter.veinToggle(),
						defaultNoiseRouter.veinRidged(),
						defaultNoiseRouter.veinGap()
				);

				NoiseGeneratorSettings modified_worldgen = new NoiseGeneratorSettings(
						defaultSettings.noiseSettings(),
						defaultSettings.defaultBlock(),
						defaultSettings.defaultFluid(),
						newNoiseRouter,
						defaultSettings.surfaceRule(),
						defaultSettings.spawnTarget(),
						defaultSettings.seaLevel(),
						defaultSettings.disableMobGeneration(),
						defaultSettings.aquifersEnabled(),
						defaultSettings.oreVeinsEnabled(),
						defaultSettings.useLegacyRandomSource());

				return modified_worldgen;
			}
		}

		return defaultSettings;
	}



}