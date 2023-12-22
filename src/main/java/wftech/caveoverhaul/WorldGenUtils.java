package wftech.caveoverhaul;

import java.util.stream.Stream;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderGetter.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.utils.LazyLoadingSafetyWrapper;

public class WorldGenUtils {

	public static NoiseRouter overworld(HolderGetter<DensityFunction> hg1, HolderGetter<NormalNoise.NoiseParameters> hg2, boolean p_255649_, boolean p_255617_) {
		
		HolderGetter hg_noise = null;
		HolderGetter hg_density_function = null;
		RegistryAccess registries;
		if(EffectiveSide.get().isClient()) {
			Level level = LazyLoadingSafetyWrapper.getClientLevel();
			registries = level.registryAccess();

			hg_noise = level.holderLookup(Registries.NOISE);
			hg_density_function = level.holderLookup(Registries.DENSITY_FUNCTION);
			
		} else {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			registries = server.registryAccess();
			Provider registryProvider = registries.asGetterLookup();
			hg_noise = registryProvider.lookupOrThrow(Registries.NOISE);
			hg_density_function = registryProvider.lookupOrThrow(Registries.DENSITY_FUNCTION);			
		}

		Registry<DensityFunction> registry_df = registries.registryOrThrow(Registries.DENSITY_FUNCTION);
		Registry<NoiseParameters> registry_np = registries.registryOrThrow(Registries.NOISE);
		
		DensityFunction densityfunction = DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.AQUIFER_BARRIER), 0.5D);
		DensityFunction densityfunction1 = DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
		DensityFunction densityfunction2 = DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143D);
		DensityFunction densityfunction3 = DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.AQUIFER_LAVA));
		
		
		DensityFunction densityfunction4 = NoiseRouterData.getFunction(hg_density_function, NoiseRouterData.SHIFT_X);
		DensityFunction densityfunction5 = NoiseRouterData.getFunction(hg_density_function, NoiseRouterData.SHIFT_Z);
		DensityFunction densityfunction6 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, registry_np.getHolderOrThrow(p_255649_ ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
		DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, registry_np.getHolderOrThrow(p_255649_ ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
		DensityFunction densityfunction8 = NoiseRouterData.getFunction(hg_density_function, p_255649_ ? NoiseRouterData.FACTOR_LARGE : (p_255617_ ? NoiseRouterData.FACTOR_AMPLIFIED : NoiseRouterData.FACTOR));
		DensityFunction densityfunction9 = NoiseRouterData.getFunction(hg_density_function, p_255649_ ? NoiseRouterData.DEPTH_LARGE : (p_255617_ ? NoiseRouterData.DEPTH_AMPLIFIED : NoiseRouterData.DEPTH));
		DensityFunction densityfunction10 = NoiseRouterData.noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
		DensityFunction densityfunction14 = NoiseRouterData.postProcess(NoiseRouterData.getFunction(hg_density_function, NoiseRouterData.SLOPED_CHEESE));
		DensityFunction densityfunction15 = NoiseRouterData.getFunction(hg_density_function, NoiseRouterData.Y);
		
		int i = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_224495_) -> {
			 return p_224495_.minY;
		}).min().orElse(-DimensionType.MIN_Y * 2);
		int j = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_224457_) -> {
			 return p_224457_.maxY;
		}).max().orElse(-DimensionType.MIN_Y * 2);
		DensityFunction densityfunction16 = NoiseRouterData.yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.ORE_VEININESS), 1.5D, 1.5D), i, j, 0);
		float f = 4.0F;
		DensityFunction densityfunction17 = NoiseRouterData.yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.ORE_VEIN_A), 4.0D, 4.0D), i, j, 0).abs();
		DensityFunction densityfunction18 =NoiseRouterData. yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.ORE_VEIN_B), 4.0D, 4.0D), i, j, 0).abs();
		DensityFunction densityfunction19 = DensityFunctions.add(DensityFunctions.constant((double)-0.08F), DensityFunctions.max(densityfunction17, densityfunction18));
		DensityFunction densityfunction20 = DensityFunctions.noise(registry_np.getHolderOrThrow(Noises.ORE_GAP));
		return new NoiseRouter(
				densityfunction, 
				densityfunction1, 
				densityfunction2, 
				densityfunction3, 
				densityfunction6, 
				densityfunction7, 
				NoiseRouterData.getFunction(hg_density_function, p_255649_ ? NoiseRouterData.CONTINENTS_LARGE : NoiseRouterData.CONTINENTS), 
				NoiseRouterData.getFunction(hg_density_function, p_255649_ ? NoiseRouterData.EROSION_LARGE : NoiseRouterData.EROSION), 
				densityfunction9, 
				NoiseRouterData.getFunction(hg_density_function, NoiseRouterData.RIDGES), 
				NoiseRouterData.slideOverworld(
						p_255617_, 
						DensityFunctions.add(
								densityfunction10, 
								DensityFunctions.constant(-0.703125D)
								).clamp(-64.0D, 64.0D)),
				densityfunction14, //replace
				densityfunction16, 
				densityfunction19, 
				densityfunction20);
	}
	
	public static boolean checkIfLikelyOverworld(NoiseSettings settings) {		
		boolean rightHeight = settings.height() == 384;
		boolean rightDepth = settings.minY() == -64;
		boolean rightRatioVertical = settings.noiseSizeHorizontal() == 1;
		boolean rightRatioHorizontal = settings.noiseSizeVertical() == 2;
		
		return rightHeight && rightDepth && rightRatioVertical && rightRatioHorizontal;
	}
}
