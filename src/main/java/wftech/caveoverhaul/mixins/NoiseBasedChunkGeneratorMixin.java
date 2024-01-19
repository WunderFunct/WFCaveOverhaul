package wftech.caveoverhaul.mixins;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.biomemodifiers.AddCarversBiomeModifier;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

	/*
	 *     public void applyCarvers(WorldGenRegion p_224224_, long p_224225_, RandomState p_224226_, BiomeManager p_224227_, StructureManager p_224228_, ChunkAccess p_224229_, GenerationStep.Carving p_224230_) {

	 */
	@Inject(method="applyCarvers(Lnet/minecraft/server/level/WorldGenRegion;JLnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/GenerationStep$Carving;)V", 
			//locals = LocalCapture.CAPTURE_FAILHARD, 
			at = @At("RETURN"))
	private void applyCarversInject(WorldGenRegion wgr, long unklong, RandomState randState, BiomeManager biomeMgr, StructureManager strMgr, ChunkAccess chunk, GenerationStep.Carving step, CallbackInfo ci) {
		List<Holder<ConfiguredWorldCarver>> entries = AddCarversBiomeModifier.POSTGEN_ADD_ULTRALARGE_NOISE_FEATURES;
		Random random = new Random();
		String searchName = "_final_stage";
		for(Holder<ConfiguredWorldCarver> entry: entries) {
			if(entry.unwrapKey().get().location().toString().contains(searchName)) {
				entry.value().carve(null, chunk, null, null, null, null, null);
			}
		}
	}
}
