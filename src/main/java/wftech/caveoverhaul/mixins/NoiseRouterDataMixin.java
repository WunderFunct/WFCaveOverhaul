package wftech.caveoverhaul.mixins;

import java.util.Iterator;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mixin(NoiseRouterData.class)
public class NoiseRouterDataMixin {
	
	/*
	 * Generate a new initial NoiseRouterWithOnlyNoises which deletes underground caves and re-map...
	 */
	@Inject(method = "createNoiseRouter(Lnet/minecraft/world/level/levelgen/NoiseSettings;JLnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/WorldgenRandom$Algorithm;Lnet/minecraft/world/level/levelgen/NoiseRouterWithOnlyNoises;)Lnet/minecraft/world/level/levelgen/NoiseRouter;", 
			at = @At("RETURN"), remap=true, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void createNoiseRouterInject(
			NoiseSettings noiseSettings, 
			long longa, 
			Registry registry, 
			WorldgenRandom.Algorithm alg, 
			NoiseRouterWithOnlyNoises noiseRouterEtc, 
			CallbackInfoReturnable<NoiseRouter> cir, 
			boolean bool1, 
			PositionalRandomFactory prf,
			Map map,
			DensityFunction.Visitor visitor,
			DensityFunction.Visitor visitor2,
			NoiseRouterWithOnlyNoises noiserouterwithonlynoises, 
			PositionalRandomFactory positionalrandomfactory1, 
			PositionalRandomFactory positionalrandomfactory2
			) {
		
		NoiseRouterWithOnlyNoises overworld = BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(new ResourceLocation("minecraft", "overworld")).noiseRouter();

		//noiseSettings.getCellHeight();
		//overworld.noiseSettings().getCellHeight()
		boolean _bool1 = noiseRouterEtc.barrierNoise().toString().contains("aquifer_barrier");
		boolean _bool2 = noiseRouterEtc.fluidLevelFloodednessNoise().toString().contains("aquifer_fluid_level_floodedness");
		boolean _bool3 = noiseRouterEtc.fluidLevelSpreadNoise().toString().contains("aquifer_fluid_level_spread");
		boolean _bool4 = noiseRouterEtc.lavaNoise().toString().contains("aquifer_lava");
		boolean _bool5 = noiseRouterEtc.temperature().toString().contains("temperature"); //
		boolean _bool6 = noiseRouterEtc.vegetation().toString().contains("vegetation"); //
		boolean _bool7 = noiseRouterEtc.continents().toString().contains("overworld/continents");
		boolean _bool8 = noiseRouterEtc.erosion().toString().contains("overworld/erosion");
		boolean _bool9 = noiseRouterEtc.depth().toString().contains("overworld/depth");
		boolean _bool10 = noiseRouterEtc.ridges().toString().contains("overworld/ridges");
		boolean _bool11 = noiseRouterEtc.initialDensityWithoutJaggedness().toString().contains("overworld/depth"); //
		boolean _bool12 = noiseRouterEtc.veinToggle().toString().contains("ore_veininess"); //
		boolean _bool13 = noiseRouterEtc.veinRidged().toString().contains("ore_vein_a"); //
		boolean _bool14 = noiseRouterEtc.veinGap().toString().contains("ore_gap");
		boolean _bool15 = noiseSettings.minY() == -64;
		boolean _bool16 = noiseSettings.height() == 384;
		boolean _bool17 = noiseRouterEtc.finalDensity().toString().contains("sloped_cheese");

		
		// Solution of last resort
		boolean isInOverworldPathway = _bool1 && 
				_bool2 && 
				_bool3 && 
				_bool4 && 
				_bool5 && 
				_bool6 && 
				_bool7 && 
				_bool8 && 
				_bool9 && 
				_bool10 && 
				_bool11 && 
				_bool12 && 
				_bool13 && 
				_bool14 && 
				_bool15 && 
				_bool16 && 
				_bool17;
		
		if(!isInOverworldPathway) {
			return;
		}

		DensityFunction newFinalDensity = NoiseRouterData.postProcess(
				noiseSettings, NoiseRouterData.getFunction(NoiseRouterData.SLOPED_CHEESE));

	    noiserouterwithonlynoises = new NoiseRouterWithOnlyNoises(
				noiserouterwithonlynoises.barrierNoise(), 
				noiserouterwithonlynoises.fluidLevelFloodednessNoise(), 
				noiserouterwithonlynoises.fluidLevelSpreadNoise(), 
				noiserouterwithonlynoises.lavaNoise(), 
				noiserouterwithonlynoises.temperature(), 
				noiserouterwithonlynoises.vegetation(), 
				noiserouterwithonlynoises.continents(), 
				noiserouterwithonlynoises.erosion(), 
				noiserouterwithonlynoises.depth(), 
				noiserouterwithonlynoises.ridges(), 
				noiserouterwithonlynoises.initialDensityWithoutJaggedness(), 
				newFinalDensity,
				noiserouterwithonlynoises.veinToggle(), 
				noiserouterwithonlynoises.veinRidged(), 
				noiserouterwithonlynoises.veinGap()
				);
	    
	    noiserouterwithonlynoises = noiserouterwithonlynoises.mapAll(visitor2);
	    
	    cir.setReturnValue(new NoiseRouter(
	    		noiserouterwithonlynoises.barrierNoise(), 
	    		noiserouterwithonlynoises.fluidLevelFloodednessNoise(), 
	    		noiserouterwithonlynoises.fluidLevelSpreadNoise(), 
	    		noiserouterwithonlynoises.lavaNoise(), 
	    		positionalrandomfactory1, 
	    		positionalrandomfactory2, 
	    		noiserouterwithonlynoises.temperature(), 
	    		noiserouterwithonlynoises.vegetation(), 
	    		noiserouterwithonlynoises.continents(), 
	    		noiserouterwithonlynoises.erosion(), 
	    		noiserouterwithonlynoises.depth(), 
	    		noiserouterwithonlynoises.ridges(), 
	    		noiserouterwithonlynoises.initialDensityWithoutJaggedness(), 
	    		noiserouterwithonlynoises.finalDensity(), 
	    		noiserouterwithonlynoises.veinToggle(), 
	    		noiserouterwithonlynoises.veinRidged(), 
	    		noiserouterwithonlynoises.veinGap(), 
	    		(new OverworldBiomeBuilder()).spawnTarget()));
	}
	
}
