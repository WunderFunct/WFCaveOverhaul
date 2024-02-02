package wftech.caveoverhaul.mixins;

import net.minecraft.data.BuiltinRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import wftech.caveoverhaul.WorldGenUtils;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.utils.FabricUtils;

@Mixin(RandomState.class)
public class RandomStateMixin {

	@ModifyVariable(method = "<init>(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/Registry;J)V",
			at = @At("HEAD"),
			remap=true)
	private static NoiseGeneratorSettings changeSettingsToOverworld(NoiseGeneratorSettings defaultSettings,
																	NoiseGeneratorSettings noiseGeneratorSettingsIn, Registry<NormalNoise.NoiseParameters> noiseParametersRegistry, final long longIn) {

		//WorldgenRevisited.LOGGER.error("[WorldgenRevisited::RandomStateMixin] Accessed");

		boolean enable_cheese_and_spaghetti_caves = false;
		if(enable_cheese_and_spaghetti_caves) {
			return defaultSettings;
		}

		Registry<NoiseGeneratorSettings> noiseReg = BuiltinRegistries.NOISE_GENERATOR_SETTINGS;

		NoiseGeneratorSettings overworldNoise = noiseReg.getOrThrow(NoiseGeneratorSettings.OVERWORLD);

		int idIn = noiseReg.getId(defaultSettings);
		int idOverworld = noiseReg.getId(overworldNoise);


		if (WorldGenUtils.checkIfSameNGS(defaultSettings, overworldNoise, longIn)) {
			//WorldgenRevisited.LOGGER.error("[WorldgenRevisited::RandomStateMixin] Returning modified settings 1");

			NoiseGeneratorSettings modified_worldgen = new NoiseGeneratorSettings(
					defaultSettings.noiseSettings(),
					defaultSettings.defaultBlock(),
					defaultSettings.defaultFluid(),
					WorldGenUtils.overworld(),
					defaultSettings.surfaceRule(), //SurfaceRuleData.overworld()
					defaultSettings.spawnTarget(), //(new OverworldBiomeBuilder()).spawnTarget()
					defaultSettings.seaLevel(),
					defaultSettings.disableMobGeneration(),
					defaultSettings.aquifersEnabled(),
					defaultSettings.oreVeinsEnabled(),
					defaultSettings.useLegacyRandomSource()); //63, false, true, true, false

			return modified_worldgen;
		}

		for(ResourceLocation key: noiseReg.keySet()) {
			NoiseGeneratorSettings ngs_noise = noiseReg.get(key);
			//WorldgenRevisited.LOGGER.error("[WorldgenRevisited::RandomStateMixin] Noise type key = " + key + ", id = " + idIn + " w/ overworld id " + idOverworld);
			//the second check is to make sure it's actually the overworld we're changing
			if(key.getPath().toLowerCase().contains("overworld") && idIn == idOverworld /*&& (defaultSettings == ngs_noise)*/) {
				//WorldgenRevisited.LOGGER.error("[WorldgenRevisited::RandomStateMixin] Returning modified settings 2");

				NoiseGeneratorSettings modified_worldgen = new NoiseGeneratorSettings(
						defaultSettings.noiseSettings(),
						defaultSettings.defaultBlock(),
						defaultSettings.defaultFluid(),
						WorldGenUtils.overworld(),
						defaultSettings.surfaceRule(), //SurfaceRuleData.overworld()
						defaultSettings.spawnTarget(), //(new OverworldBiomeBuilder()).spawnTarget()
						defaultSettings.seaLevel(),
						defaultSettings.disableMobGeneration(),
						defaultSettings.aquifersEnabled(),
						defaultSettings.oreVeinsEnabled(),
						defaultSettings.useLegacyRandomSource()); //63, false, true, true, false

				return modified_worldgen;
			}
		}

		return defaultSettings;
	}
	
	
	
}