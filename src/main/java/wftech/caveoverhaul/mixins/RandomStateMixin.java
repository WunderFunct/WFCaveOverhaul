package wftech.caveoverhaul.mixins;

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
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import wftech.caveoverhaul.WorldGenUtils;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.utils.FabricUtils;

@Mixin(RandomState.class)
public class RandomStateMixin {

	@ModifyVariable(method = "<init>(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)V", 
			at = @At("HEAD"), 
			remap=true)
	private static NoiseGeneratorSettings changeSettingsToOverworld(NoiseGeneratorSettings defaultSettings, 
			NoiseGeneratorSettings noiseGeneratorSettingsIn, HolderGetter<NormalNoise.NoiseParameters> holderIn, final long longIn) {

		
		HolderGetter noise = null;
		HolderGetter density_function = null;
		RegistryAccess registries;
		MinecraftServer server = FabricUtils.server;
		registries = server.registryAccess();
		Provider provider = registries.asGetterLookup();
		noise = provider.lookupOrThrow(Registries.NOISE);
		density_function = provider.lookupOrThrow(Registries.NOISE);

		Registry<NoiseGeneratorSettings> noiseReg = registries.registryOrThrow(Registries.NOISE_SETTINGS);
		
		NoiseGeneratorSettings overworldNoise = noiseReg.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
		
		for(ResourceLocation key: noiseReg.keySet()) {
			NoiseGeneratorSettings ngs_noise = noiseReg.get(key);
			if(key.getPath().toLowerCase().contains("overworld") && (defaultSettings == ngs_noise)) {

				NoiseGeneratorSettings modified_worldgen = new NoiseGeneratorSettings(
					    defaultSettings.noiseSettings(), 
					    defaultSettings.defaultBlock(), 
					    defaultSettings.defaultFluid(), 
					    WorldGenUtils.overworld(
				    		noise, 
				    		density_function, 
					        false, 
					        false),
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