package wftech.caveoverhaul.carvertypes;

import net.minecraft.util.RandomSource;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.utils.FabricUtils;

/*
The noise caves are **NOT** implemented via carvers.
Rather, all released versions of Cave Overhaul uses mixins.
Please see all mixins referring to NoiseChunkMixinUtils for implementation
details.

That said, this file and children classes contain all noise data that
NoiseChunkMixinUtils reference.
 */
public abstract class NoiseCavernBaseFixFromNewCaves {

	public static int MAX_CAVE_SIZE_Y = 20;
	public static FastNoiseLite domainWarp = null;

	public static FastNoiseLite noise = null;
	
	protected void initNoise() {
		
		if(noise != null) {
			return;
		}
		
		//Simplex
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenOptions().seed());
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFractalGain(0.3f);
		tnoise.SetFrequency(0.025f);
		tnoise.SetFractalType(FractalType.FBm);
		
		noise = tnoise;
	}
	
	public static void initNoiseStatic() {
		
		if(noise != null) {
			return;
		}
		
		//Simplex
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenOptions().seed());
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFractalGain(0.3f);
		tnoise.SetFrequency(0.025f);
		tnoise.SetFractalType(FractalType.FBm);
		
		noise = tnoise;
	}
	
	
	protected int getCaveY(float noiseValue) {
		return (int) (noiseValue * (64f));
	}
	
	public static void initDomainWarp() {
		
		if(domainWarp != null) {
			return;
		}
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenOptions().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		domainWarp = tnoise;
	}

	public static float getNoiseThreshold(float x, float z) {
		//default 0.08
		return 0.15f;
		//New system: Scale the threshold from 1 (do not draw) to 0.08 (draw) based on noise
		
		/*
		String key = x + "," + z;
		if(this.localThresholdCache.containsKey(key)) {
			return this.localThresholdCache.get(key);
		}
		
		float baseNoise = this.getCaveYNoise(x, z);
		float squishedNoise = this.ySquishThreshold(baseNoise * (float) MAX_CAVE_SIZE_Y); //apply MAX_CAVE_SIZE_Y as modeling is done with it enabled
		/*
		 * I now have a value that is 0-1. I need to transform this value to something where
		 * it falls on a scale of 0.8-1, such that 1 = out of bounds (was 0), and 0.8 = in bounds (was 1).
		 * For the original, 0 = out of range, 1 = fully in range
		 */
		/*
		squishedNoise *= (1f - 0.08f); //limits it to 0-0.92, with 0.92 being in range
		squishedNoise = 1f - squishedNoise; //Makes it 0.08 - 1.0, with 0.08 being in range
		
		this.localThresholdCache.put(key, squishedNoise);
		return squishedNoise;
		*/
	}
	
	public static float ySquish(float noiseHeight) {
		float caveOffset = ((float) MAX_CAVE_SIZE_Y) / 2f; //(float)MAX_CAVE_SIZE_Y/4f; //if 32, becomes 8. Noise is usually a normal distribution with the mean being MAX/2.
		float k = 2f; //1f = 8 tiles from 1 to 0, 2f = 4 tiles, 16f for an outgoing range of [0, 1]
		//Use https://www.desmos.com/calculator
		//desmos equation: y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(\left(-x\ +\ 32\right)\right)}}
		int dist = 2 + 1; //2f = 2, 4f = 1, 1f = 8, 3f = 1.5?, then add a +1 to account for edge squish weirdness
		if (noiseHeight > caveOffset + dist || noiseHeight < caveOffset - dist) {
			return 0f;
		}
		
		return 1f - (float) (1f / (1f + Math.exp(k * (-noiseHeight + (caveOffset)))));
		
	}
	
	public static float ySquishSatic(float noiseHeight) {
		//was k = 2f, dist = 2 + 1
		float caveOffset = ((float) MAX_CAVE_SIZE_Y) / 2f; //(float)MAX_CAVE_SIZE_Y/4f; //if 32, becomes 8. Noise is usually a normal distribution with the mean being MAX/2.
		float k = 2f; //1f = 8 tiles from 1 to 0, 2f = 4 tiles, 16f for an outgoing range of [0, 1]
		//Use https://www.desmos.com/calculator
		//desmos equation: y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(\left(-x\ +\ 32\right)\right)}}
		int dist = 2 + 1; //2f = 2, 4f = 1, 1f = 8, 3f = 1.5?, then add a +1 to account for edge squish weirdness
		if (noiseHeight > caveOffset + dist || noiseHeight < caveOffset - dist) {
			return 0f;
		}
		
		return 1f - (float) (1f / (1f + Math.exp(k * (-noiseHeight + (caveOffset)))));
		
	}
	
	public static float norm(float f) {
		return (1f + f) / 2f;
	}
	
    public int getCaveY(RandomSource p_230361_1_) {
	    return p_230361_1_.nextInt(p_230361_1_.nextInt(p_230361_1_.nextInt(120 + 64) + 1) + 1) - 64;
    }

	
    /* **CHANGED**
     * v2 change:
     * 1. disable yPos>=0 -> return no warp
     * 2. make warpslide slide from 64 to -64
     * 3. Make y > 0 iterate 1 time
     * 4. Make the warp coord offsets dependent on y pos, slide between 5 and 30?
     * 		64 -> 0: 5 -> 10
     * 		0 -> -64: 10 -> 30
     */
	protected static float getWarpedNoise(int xPos, int yPos, int zPos) {

		//CHANGED, was true
		//v2 change
		/*
		if(yPos >= 0)
		{
			return getCaveDetailsNoise(xPos, yPos, zPos);
		}
		*/
			
		if(domainWarp == null) {
			initDomainWarp();
		}
		
		Integer[] offsetsX = {-101, 71, 53, 61, 3, 13};
		//Integer[] offsetsY = {23, 29, 31, 37, 41};
		Integer[] offsetsZ = {101, 67, 59, 41, 5, 7};

		//CHANGED
		//v1
		//float warpSlide = 25f * ( -yPos / 64f);
		
		//v2
		int tYPos = yPos + 64;
		float warpSlide = 25f * ( tYPos / 128f);
		
		float yOrig = yPos / 2f;
		float yAdjPart = ( -yPos / 64f);
		float yAdj = 2f - yAdjPart;
		
		float warpX = xPos;
		float warpY = yPos;
		float warpZ = zPos;
		//warpY = yAdj;
		//CHANGED
		//v1
		//int iterAmounts = 3;
		//int warpOffset = 20;
		
		//v2 change
		int iterAmounts = yPos >= 0 ? 2 : 3;
		float yPosClamped = yPos > 64 ? 64f : (float) yPos;
		float warpOffsetF = yPos >= 0 ? ((yPosClamped / 64f) * 5f) + 5f : (((-yPosClamped) / 64f) * 20f) + 10f;
		int warpOffset = Math.round(warpOffsetF);
		
		for(int i = 0; i < iterAmounts; i++) {
			//CHANGED
			//Not applying an offset to warpX is intentional.
			//The location for warpX can be anywhere, so it's ok that there's no offset. It hsould have no skew change or anything.
			warpY += domainWarp.GetNoise(warpX, warpY, warpZ) * warpSlide; //was 5 with pretty incredible results
			warpX += domainWarp.GetNoise(warpX + warpOffset, warpY + warpOffset, warpZ + warpOffset) * warpSlide;
			warpZ += domainWarp.GetNoise(warpX - warpOffset, warpY - warpOffset, warpZ - warpOffset) * warpSlide;
		}
		
		return getCaveDetailsNoise(warpX, warpY, warpZ);
	}

	//Override this for non-cavern noise
	public static float getCaveDetailsNoise(float x, float y, float z) {
		if(noise == null) {
			initNoiseStatic();
		}
		
		return noise.GetNoise(x, y, z);
	}


	
	public static boolean shouldCarveBasedOnHeightStatic(float x, float y, float z, int caveHeight, int caveY) {
		
		/*
		 * Keep in superclass
		 */
		
		int y_adj = (int) y;
		int yPos = (int) y;
		
		if(yPos < caveY || yPos > caveY + caveHeight) {
			return false;
		}
		
		int xPos = (int) x;
		int zPos = (int) z;
		
		float noiseFound = getWarpedNoise(xPos, yPos*2, zPos);
		boolean shouldCarve = noiseFound > getNoiseThreshold(xPos, zPos);
		
		return shouldCarve;
	}

}
