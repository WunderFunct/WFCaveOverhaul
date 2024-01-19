
package wftech.caveoverhaul.carvertypes.simplex;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.carvertypes.NoiseCavernBaseNewCaves;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;

public class NoiseCaveSimplexBoring extends NoiseCavernBaseSimplex {

	public NoiseCaveSimplexBoring(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}

	
	/*
	 * -64 to 0, doubling up to expand the amount of fun caves near the bottom
	 */
	private float minY = -64;
	private float maxY = 0;

	//Transforms a given y-level noise to the cave-to-be-carved's y floor
	@Override
	protected int getCaveY(float noiseValue) {
		//Original
		//return (int) (noiseValue * (64f));
		return (int) (((maxY - minY) * noiseValue) + minY);
		
		//return 4;
	}
	
	@Override
	protected boolean shouldAdjustY() {
		return false;
	}
	
	@Override
	protected float getWarpedNoise(int xPos, int yPos, int zPos) {
		return this.getCaveDetailsNoise(xPos, yPos, zPos);
	}
	
	/*
	 * Noise portions down here for easy copy and pasting
	 */
	public static FastNoiseLite yNoise = null;
	public static FastNoiseLite caveSizeNoise = null;
	public static FastNoiseLite detailNoise = null;
	public static int seedOffset = -2;
	
	private void initYNoise() {

		int seed = (int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
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
		
		int seed = (int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
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
	
	public static void initCaveHeightMapStatic() {
		
		int seed = (int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
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
	
	public void initDetailNoise() {

		/*
		 * 
            this.noiseThreshold = 0.95;
            this.fractalOctaves = 1;
            this.fractalGain = 0.3;
            this.fractalFrequency = 0.03;
            this.numGenerators = 2;
            this.yAdjust = true;
            this.yAdjustF1 = 0.9;
            this.yAdjustF2 = 0.9;
            this.noiseType = "CubicFractal";
		 */

		
		int seed = (int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed(); //(int) this.ctx.randomState().legacyLevelSeed();
		seed += seedOffset + 1;

		/*
		FastNoise tnoise = new FastNoise();
		tnoise.SetSeed(0);
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.SimplexFractal);
		tnoise.SetFractalGain(0.3f);
		tnoise.SetFrequency(0.025f);
		//tnoise.SetFractalType(FractalType.FBM);
		tnoise.SetFractalType(FractalType.FBM);
		*/
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed(seed);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.025f); //was 0.01
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(0.3f); //seems to top out at 3.5 though
		tnoise.SetFractalOctaves(1);
		//tnoise.SetFractalLacunarity(0.2f); //<-- 0.1?
		
		detailNoise = tnoise;
	}

	@Override
	float getCaveYNoise(float x, float z) {
		if(yNoise == null) {
			initYNoise();
		}
		
		return yNoise.GetNoise(x, z);
	}


	@Override
	protected float getCaveThicknessNoise(float x, float z) {
		
		if(caveSizeNoise == null) {
			initCaveHeightMap();
		}
		
		return caveSizeNoise.GetNoise(x, z);
		
		
		//return 128;
	}
	
	@Override
	protected float getCaveDetailsNoise(float x, float y, float z) {
		if(detailNoise == null) {
			initDetailNoise();
		}
		
		return detailNoise.GetNoise(x, y, z);
	}

}
