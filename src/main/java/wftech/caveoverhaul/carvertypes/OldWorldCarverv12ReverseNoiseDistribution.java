package wftech.caveoverhaul.carvertypes;

import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import java.util.Random;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import wftech.caveoverhaul.AirOnlyAquifer;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;

public class OldWorldCarverv12ReverseNoiseDistribution extends OldWorldCarverv12 {

	public OldWorldCarverv12ReverseNoiseDistribution(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
	@Override
    protected boolean shouldCarve(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, Random random, ChunkPos chunkPos_) {

        
		if(NoiseCavernBottomLayer1.caveSizeNoise == null) {
			NoiseCavernBottomLayer1.initCaveHeightMapStatic();
		}
		if(NoiseCavernBottomLayer2.caveSizeNoise == null) {
			NoiseCavernBottomLayer2.initCaveHeightMapStatic();
		}
		if(NoiseCavernMiddleLayer1.caveSizeNoise == null) {
			NoiseCavernMiddleLayer1.initCaveHeightMapStatic();
		}
		if(NoiseCavernMiddleLayer2.caveSizeNoise == null) {
			NoiseCavernMiddleLayer2.initCaveHeightMapStatic();
		}
		if(NoiseCavernTopLayer1.caveSizeNoise == null) {
			NoiseCavernTopLayer1.initCaveHeightMapStatic();
		}
		if(NoiseCavernTopLayer2.caveSizeNoise == null) {
			NoiseCavernTopLayer2.initCaveHeightMapStatic();
		}
		if(NoiseCavernTopLayer3.caveSizeNoise == null) {
			NoiseCavernTopLayer3.initCaveHeightMapStatic();
		}
		
		FastNoiseLite[] caveThicknessCarvers = {
				NoiseCavernBottomLayer1.caveSizeNoise, 
				NoiseCavernBottomLayer2.caveSizeNoise, 
				NoiseCavernMiddleLayer1.caveSizeNoise, 
				NoiseCavernMiddleLayer2.caveSizeNoise, 
				NoiseCavernTopLayer1.caveSizeNoise,
				NoiseCavernTopLayer2.caveSizeNoise,
				NoiseCavernTopLayer3.caveSizeNoise
		};
		
		ChunkPos chunkPos = level.getPos();
		int x = chunkPos.getBlockX(0);
		int z = chunkPos.getBlockZ(0);
		
		float totalHeights = 0;
		float numChecked = 0;
		float maxHeight = 0;
		for(FastNoiseLite entry: caveThicknessCarvers) {
			float noiseFound = entry.GetNoise(x, z) + 1f;
			totalHeights += noiseFound;
			numChecked += 2f;
			if(noiseFound > maxHeight) {
				maxHeight = noiseFound;
			}
		}
		
		float avgHeight = totalHeights / numChecked;
		//float avgHeight = maxHeight;
		avgHeight *= (float) NoiseCavernBaseNewCavesDEPRECATED.MAX_CAVE_SIZE_Y;
		float noiseToSquish = avgHeight = ((1f + avgHeight) / 2f) * (float) NoiseCavernBaseNewCavesDEPRECATED.MAX_CAVE_SIZE_Y;
		float squished = NoiseCavernBaseNewCavesDEPRECATED.ySquishSatic(noiseToSquish);

		/*
		 * 0.15 or 0.1 if squished = 0 (avg of heights implies low density)
		 * 0.5 if caves form.
		 */
		
		/*
		float odds = avgHeight <= 10f ? 0.10f : 0.05f;
		float odds2 = avgHeight * 2;
		odds2 = odds2 > 1f ? 1f : odds2;
		odds2 = 1f - odds2;
		odds2 *= 0.1;
		odds2 = odds2 < 0.05 ? 0.05f : odds2;
		*/
		
		//was min = 0.03, max = 0.07
		float odds2 = 1f - squished;
		odds2 *= 0.05;
		odds2 = odds2 < 0.05 ? 0.07f : odds2;
		
		return random.nextFloat() <= odds2;
    }
	
	@Override
	public boolean isStartChunk(CaveCarverConfiguration p_224894_, Random p_224895_) {
		return true;
	}
	
}
