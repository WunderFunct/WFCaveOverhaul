package wftech.caveoverhaul.carvertypes;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.utils.FabricUtils;

//was NoiseCavernBaseFixFromNewCaves
public class NoiseCavernMiddleLayer2 extends NoiseCavernBaseFixFromNewCaves {

	/*
	 * -32 to 0
	 */
	public static float minY = 0;
	public static float maxY = 32;

	//Transforms a given y-level noise to the cave-to-be-carved's y floor
	@Override
	protected int getCaveY(float noiseValue) {
		//Original
		//return (int) (noiseValue * (64f));
		return (int) (((maxY - minY) * noiseValue) + minY);
	}
	
	/*
	 * Noise portions down here for easy copy and pasting
	 */
	public static FastNoiseLite yNoise = null;
	public static FastNoiseLite caveSizeNoise = null;
	public static int seedOffset = 7;
	
	private void initYNoise() {

		int seed = (int) FabricUtils.server.getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
		seed += seedOffset;
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed(seed);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		//tnoise.SetFractalType(FractalType.FBM);
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(2.5f);
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.1f);
		
		yNoise = tnoise;
	}
	
	public void initCaveHeightMap() {
		
		int seed = (int) FabricUtils.server.getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
		seed += seedOffset + 1;
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed(seed);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.015f); //was 0.01
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(1.3f); //seems to top out at 3.5 though
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.2f); //<-- 0.1?
		
		caveSizeNoise = tnoise;
	}
	
	/*
	public static void initCaveHeightMapStatic() {
		
		int seed = (int) FabricUtils.server.getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
		seed += seedOffset + 1;
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed(seed);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.015f); //was 0.01
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(1.3f); //seems to top out at 3.5 though
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.2f); //<-- 0.1?
		
		caveSizeNoise = tnoise;
	}
	*/

	/*
	 * Static calls
	 */

	//Do we do this carver during the noise stage or the carver stage?
	public static boolean isForNoiseStage() {
		return true;
	}
	
	public static void initYNoiseStatic() {
		if(yNoise != null) {
			return;
		}

		int seed = (int) FabricUtils.server.getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
		seed += seedOffset;
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed(seed);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		//tnoise.SetFractalType(FractalType.FBM);
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(2.5f);
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.1f);
		
		yNoise = tnoise;
	}
	
	public static void initCaveHeightMapStatic() {
		if(caveSizeNoise != null) {
			return;
		}
		
		int seed = (int) FabricUtils.server.getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
		seed += seedOffset + 1;
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed(seed);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.015f); //was 0.01
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(1.3f); //seems to top out at 3.5 though
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.2f); //<-- 0.1?
		
		caveSizeNoise = tnoise;
	}
	
	public static boolean shouldCarve(float x, float y, float z) {
		/*
		 * Put in subclass (copy and paste)
		 */
		MutableBlockPos mPos = new MutableBlockPos();
		int earlyXPos = (int) x;
		int earlyZPos = (int) z;
		
		float caveHeightNoise = getCaveThicknessNoiseStatic(earlyXPos, earlyZPos);
		int caveHeight = 0;
		caveHeightNoise = ((1f + caveHeightNoise) / 2f) * (float) MAX_CAVE_SIZE_Y;
		float caveHeightNoiseSquished = NoiseCavernBaseFixFromNewCaves.ySquish(caveHeightNoise);
		caveHeight = (int) (caveHeightNoiseSquished * MAX_CAVE_SIZE_Y);
		if(caveHeight <= 0) {
			return false;
		}
		
		float rawNoiseY = getCaveYNoiseStatic(earlyXPos, earlyZPos);
		rawNoiseY = NoiseCavernBaseFixFromNewCaves.norm(rawNoiseY);
		rawNoiseY = rawNoiseY > 1 ? 1 : (rawNoiseY < 0 ? 0 : rawNoiseY);
		int caveY = getCaveYStatic(rawNoiseY);
		
		return NoiseCavernBaseFixFromNewCaves.shouldCarveBasedOnHeightStatic(x, y, z, caveHeight, caveY);
	}
	
	public static float getCaveThicknessNoiseStatic(int x, int z) {
		if(caveSizeNoise == null) {
			initCaveHeightMapStatic();
		}
		
		return caveSizeNoise.GetNoise(x, z);
	}
	
	public static float getCaveYNoiseStatic(int x, int z) {
		if(yNoise == null) {
			initYNoiseStatic();
		}
		
		return yNoise.GetNoise(x, z);
	}
	
	public static int getCaveYStatic(float noiseValue) {
		return (int) (((maxY - minY) * noiseValue) + minY);
	}
}
