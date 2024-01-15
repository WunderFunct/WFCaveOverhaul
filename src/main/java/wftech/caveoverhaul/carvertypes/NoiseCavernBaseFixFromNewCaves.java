package wftech.caveoverhaul.carvertypes;

import java.util.HashMap;

/*
 * Wall fix:
 * 
 * Instead of going from 1 to 0 (carve to not carve), allow for fuzzy edges where the noise threshold drops as well as the cave size height.
 * So maybe make the cave height drop off slowly, but the noise threshold drop off faster?
 * 
 * height/ysquish = y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(1\ \cdot\ \left(-x\ +\ 10\right)\right)}}
 * threshold multiplier (1 to 0, float) = y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(2\ \cdot\ \left(-x\ +\ 9\right)\right)}}
 */

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import java.util.Random;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.AirOnlyAquifer;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.RotationType3D;

/*
 * Changes:
 * Made domain warp increase as you go further down
 * yPos was reverted from *2 to *1 <- reverted back to *2
 * 		Domain warp now slides from x2 to x1 between -1 and -64
 * 
 * Undo below:
 * Swapped the initNoise function
 * Disabled domain warp yPos slide and replaced it with 2
 * Changed threshold to 0.08
 * 
 * Changed threshold back to 0.15
 * Changed yPos multiplier to *3
 * Changed it back to *2
 */

public abstract class NoiseCavernBaseFixFromNewCaves extends CaveWorldCarver {

	public static int MAX_CAVE_SIZE_Y = 20;
	
	//public static FastNoiseLite noise = null;
	//public static FastNoiseLite yNoise = null;
	//public static FastNoiseLite caveSizeNoise = null;
	public static FastNoiseLite domainWarp = null;
	private CarvingContext ctx;
	private CaveCarverConfiguration cfg;
	private ChunkAccess level;
	private Function<BlockPos, Holder<Biome>> biome;
	private Random random;
	private Aquifer aquifer;
	private CarvingMask mask;
	private HashMap<String, Float> localThresholdCache;
	
	public NoiseCavernBaseFixFromNewCaves(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
		// TODO Auto-generated constructor stub
	}
	
	abstract float getCaveYNoise(float x, float z);
	abstract float getCaveThicknessNoise(float x, float z);

	public static FastNoiseLite noise = null;
	
	protected void initNoise() {
		
		if(noise != null) {
			return;
		}
		
		//Simplex
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		//tnoise.SetRotationType3D(RotationType3D.ImproveXZPlanes);
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
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		//tnoise.SetRotationType3D(RotationType3D.ImproveXZPlanes);
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
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		domainWarp = tnoise;
	}
	
	@Override
	public boolean carve(
		CarvingContext ctx, 
		CaveCarverConfiguration cfg, 
		ChunkAccess level, 
		Function<BlockPos, Holder<Biome>> pos2BiomeMapping, 
		Random random, 
		Aquifer _aquifer, 
		ChunkPos chunkPos_, 
		CarvingMask mask) {

		this.ctx = ctx;
		this.cfg = cfg;
		this.level = level;
		this.biome = pos2BiomeMapping;
		this.random = random;
		this.mask = mask;
		this.localThresholdCache = new HashMap<String, Float>();
		
		if(random.nextFloat() <= 0.1) {
			return true;
		}
		
		Aquifer airAquifer = new AirOnlyAquifer(level, false); //random.nextFloat() <= Config.PERC_PIERCE_SURFACE.get()
		this.aquifer = airAquifer;
		//this.aquifer = _aquifer;
		
		ChunkPos chunkPos = level.getPos();
		BlockPos _basePos = chunkPos.getWorldPosition();

		MutableBlockPos mPos = new BlockPos.MutableBlockPos();
		MutableBlockPos chunkCenter = new BlockPos.MutableBlockPos();
		MutableBlockPos unkPos = new BlockPos.MutableBlockPos();
		MutableBoolean mBool = new MutableBoolean();
		
		LevelChunkSection[] sections = level.getSections();
		
		for(int x_offset = 0; x_offset < 16; x_offset++) {
			for(int z_offset = 0; z_offset < 16; z_offset++) {
				int height = level.getHeight(Types.WORLD_SURFACE_WG, chunkPos.getBlockX(x_offset), chunkPos.getBlockZ(z_offset));
				int earlyXPos = chunkPos.getBlockX(x_offset);
				int earlyZPos = chunkPos.getBlockZ(z_offset);
				//Apply a box blur to our thickness value to fight a weeeeeird problem with random XZ planes of spiked values.
				//Probably a fastnoiselite artifact.
				float chn_o = this.getCaveThicknessNoise(earlyXPos, earlyZPos);
				float chn_1 = this.getCaveThicknessNoise(earlyXPos + 1, earlyZPos);
				float chn_2 = this.getCaveThicknessNoise(earlyXPos + 1, earlyZPos + 1);
				float chn_3 = this.getCaveThicknessNoise(earlyXPos + 1, earlyZPos - 1);
				float chn_4 = this.getCaveThicknessNoise(earlyXPos - 1, earlyZPos);
				float chn_5 = this.getCaveThicknessNoise(earlyXPos - 1, earlyZPos + 1);
				float chn_6 = this.getCaveThicknessNoise(earlyXPos - 1, earlyZPos - 1);
				float chn_7 = this.getCaveThicknessNoise(earlyXPos, earlyZPos + 1);
				float chn_8 = this.getCaveThicknessNoise(earlyXPos, earlyZPos - 1);
				float caveHeightNoise = chn_o + chn_1 + chn_2 + chn_3 + chn_4 + chn_5 + chn_6 + chn_7 + chn_8;
				caveHeightNoise /= 9f;
				int caveHeight = 0;

				int xt = 9856;
				int yt = 117;
				int zt = 1336;
				
				boolean withinMainCave = true;
				if(caveHeightNoise < 2) {
					caveHeightNoise = ((1f + caveHeightNoise) / 2f) * (float) MAX_CAVE_SIZE_Y;
					float caveHeightNoiseSquished = this.ySquish(caveHeightNoise);
					caveHeight = (int) (caveHeightNoiseSquished * MAX_CAVE_SIZE_Y);

					if(caveHeight <= 0) {
						//v1
						//CHANGED
						//continue;
						
						//v2
						withinMainCave = false;
					}
				} else {
					caveHeight = (int) caveHeightNoise;
				}
				
				float rawNoiseY = this.getCaveYNoise(earlyXPos, earlyZPos);
				rawNoiseY = this.norm(rawNoiseY);
				rawNoiseY = rawNoiseY > 1 ? 1 : (rawNoiseY < 0 ? 0 : rawNoiseY);
				int caveY = this.getCaveY(rawNoiseY); //(int) (rawNoiseY * (64f));
				//CHANGED
				//caveY = 4;
				//caveHeight = 128;
				caveHeight = 20;
				
				//for(int y_unadj = caveY + caveHeight; y_unadj > caveY; y_unadj--) {
				for(int y_unadj = caveY + caveHeight; y_unadj > caveY; y_unadj--) {
					int y_adj = y_unadj;
					int yPos = y_unadj;
					//CHANGED this.shouldAdjustY()
					if(false){
						y_adj = y_unadj - 64;
						yPos = y_unadj - 64;
					}
					if(y_adj <= -64) {
						CaveOverhaul.LOGGER.error("[Cave Overhaul] NoiseCarverTest below -64!");
						continue;
					}

					
					int xPos = chunkPos.getBlockX(x_offset);
					//int yPos = y_unadj - 64;
					//int y_adj = y_unadj - 64;
					int zPos = chunkPos.getBlockZ(z_offset);
					mPos.set(xPos, y_adj, zPos);
					if(!level.getBlockState(mPos).getMaterial().isSolid()) {
						continue;
					}

					float nf_o = this.getWarpedNoise(xPos, yPos*2, zPos);
					float noiseFound = nf_o;
					
					if(nf_o <= this.getNoiseThreshold(xPos, zPos)) {
						float nf_1 = this.getWarpedNoise(xPos + 1, yPos*2, zPos);
						float nf_2 = this.getWarpedNoise(xPos - 1, yPos*2, zPos);
						float nf_3 = this.getWarpedNoise(xPos + 1, yPos*2, zPos + 1);
						float nf_4 = this.getWarpedNoise(xPos - 1, yPos*2, zPos + 1);
						float nf_5 = this.getWarpedNoise(xPos + 1, yPos*2, zPos - 1);
						float nf_6 = this.getWarpedNoise(xPos - 1, yPos*2, zPos - 1);
						float nf_7 = this.getWarpedNoise(xPos, yPos*2, zPos + 1);
						float nf_8 = this.getWarpedNoise(xPos, yPos*2, zPos - 1);
						noiseFound = nf_o + nf_1 + nf_2 + nf_3 + nf_4 + nf_5 + nf_6 + nf_7 + nf_8;
						noiseFound /= 9f;
					}
					boolean shouldCarve = noiseFound > this.getNoiseThreshold(xPos, zPos); //noiseFound > this.getNoiseThreshold(xPos, zPos); //was 0.08
					
					if(shouldCarve) {

						if(earlyXPos == xt && earlyZPos == zt) {
							//CaveOverhaul.LOGGER.error("[New Caves] 5@"+this.getClass().getName()+" -> Carving @ " + y_adj);
						}
						
						chunkCenter.set(chunkPos.getBlockX(0), yPos, chunkPos.getBlockX(0));
						if(mPos.distManhattan(chunkCenter) > 8) {
							//continue;
						}
						
						try {
							boolean setToLiquid = this.carveBlock(ctx, cfg, level, pos2BiomeMapping, mask, mPos, unkPos, aquifer, mBool);
							if(!setToLiquid) {
								mask.set(mPos.getX(), mPos.getY(), mPos.getZ());
							}
							//BlockState reqState = _aquifer.computeSubstance(new DensityFunction.SinglePointContext(mPos.getX(), mPos.getY(), mPos.getZ()), 0.0D);
							LevelAccessor access = level.getWorldForge();
						} catch (ArrayIndexOutOfBoundsException e){
							CaveOverhaul.LOGGER.error("[Cave Overhaul] NoiseCarverTest real error");
						}
					}
				}
			}
		}
		
		if(true) {
			//String[] throwSomething = {"a"};
			//String t = throwSomething[1000];
		}
		
		return true;
	}
	
	protected boolean shouldAdjustY() {
		return true;
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
	
	//Apply a sigmoid to the cave height so I quickly go from MAX_HEIGHT to MIN_HEIGHT, but only at the "boundary" of noise; leads to less
	//linear ugliness
	//ySquish defaults **were** k=2f, dist=2+1
	//Changed to k=1f, dist=8+1 due to the introduction of threshold squishing
	//Threshold squish is k=2, dist = 3+1, caveOffset = 9 (manually chosen based on modeling, using an assumed default cave height of 20.
	//The curve had to flatten to allow time for threshold squishing to work its magic
	public float ySquishThreshold(float noiseHeight) {
		float caveOffset = ((float) MAX_CAVE_SIZE_Y) / 2f; //(float)MAX_CAVE_SIZE_Y/4f; //if 32, becomes 8. Noise is usually a normal distribution with the mean being MAX/2.
		caveOffset = 9f;
		float k = 2f; //1f = 8 tiles from 1 to 0, 2f = 4 tiles, 16f for an outgoing range of [0, 1]
		//Use https://www.desmos.com/calculator
		//desmos equation: y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(\left(-x\ +\ 32\right)\right)}}
		//The 3+1 is intentional
		int dist = 3 + 1; //2f = 2, 4f = 1, 1f = 8, 3f = 1.5?, then add a +1 to account for edge squish weirdness
		if (noiseHeight > caveOffset + dist || noiseHeight < caveOffset - dist) {
			return 0f;
		}
		
		return 1f - (float) (1f / (1f + Math.exp(k * (-noiseHeight + (caveOffset)))));
		
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
	
    public int getCaveY(Random p_230361_1_) {
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
		
		int xPos = (int) x;
		int zPos = (int) z;
		
		if(yPos < caveY || yPos > caveY + caveHeight) {
			return false;
		}
		
		float noiseFound = getWarpedNoise(xPos, yPos*2, zPos);
		
		boolean shouldCarve = noiseFound > getNoiseThreshold(xPos, zPos);
		
		return shouldCarve;
	}
	
	/*
	public static float getCaveThicknessNoiseStatic(int x, int z) {
		return 0;
	}
	
	public static float getCaveYNoiseStatic(int x, int z) {
		return 0;
	}
	
	public static int getCaveYStatic(float y) {
		return 0;
	}
	*/

}
