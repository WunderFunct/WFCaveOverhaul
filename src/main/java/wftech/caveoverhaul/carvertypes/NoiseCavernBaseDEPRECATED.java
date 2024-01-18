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
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Aquifer;
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


public abstract class NoiseCavernBaseDEPRECATED extends CaveWorldCarver {

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
	
	public NoiseCavernBaseDEPRECATED(Codec<CaveCarverConfiguration> p_159194_) {
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
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
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
	
	protected void initDomainWarp() {
		
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
				float caveHeightNoise = this.getCaveThicknessNoise(earlyXPos, earlyZPos);
				int caveHeight = 0;
				if(caveHeightNoise < 2) {
					caveHeightNoise = ((1f + caveHeightNoise) / 2f) * (float) MAX_CAVE_SIZE_Y;
					float caveHeightNoiseSquished = this.ySquish(caveHeightNoise);
					caveHeight = (int) (caveHeightNoiseSquished * MAX_CAVE_SIZE_Y);
					if(caveHeight <= 0) {
						continue;
					}
				} else {
					caveHeight = (int) caveHeightNoise;
				}
				float rawNoiseY = this.getCaveYNoise(earlyXPos, earlyZPos);
				rawNoiseY = this.norm(rawNoiseY);
				rawNoiseY = rawNoiseY > 1 ? 1 : (rawNoiseY < 0 ? 0 : rawNoiseY);
				int caveY = this.getCaveY(rawNoiseY); //(int) (rawNoiseY * (64f));
				for(int y_unadj = caveY + caveHeight; y_unadj > caveY; y_unadj--) {
					int y_adj = y_unadj;
					int yPos = y_unadj;
					if(this.shouldAdjustY()){
						y_adj = y_unadj - 64;
						yPos = y_unadj - 64;
					}
					int xPos = chunkPos.getBlockX(x_offset);
					//int yPos = y_unadj - 64;
					//int y_adj = y_unadj - 64;
					int zPos = chunkPos.getBlockZ(z_offset);
					mPos.set(xPos, y_adj, zPos);
					if(level.getBlockState(mPos).isAir()) {
						continue;
					}
					float noiseFound = this.getWarpedNoise(xPos, yPos, zPos);
					boolean shouldCarve = noiseFound > this.getNoiseThreshold(xPos, zPos); //was 0.08
					if(shouldCarve) {
						
						chunkCenter.set(chunkPos.getBlockX(0), yPos, chunkPos.getBlockX(0));
						if(mPos.distManhattan(chunkCenter) > 8) {
							//continue;
						}
						
						try {

							boolean setToLiquid = this.carveBlock(ctx, cfg, level, pos2BiomeMapping, mask, mPos, unkPos, aquifer, mBool);
							if(!setToLiquid) {
								mask.set(mPos.getX(), mPos.getY(), mPos.getZ());
							}							
							LevelAccessor access = level.getWorldForge();
						} catch (ArrayIndexOutOfBoundsException e){
						}
					}
				}
			}
		}
		
		return true;
	}
	
	protected boolean shouldAdjustY() {
		return true;
	}

	protected float getNoiseThreshold(float x, float z) {
		//default 0.08
		return 0.08f;
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
	
	public float ySquish(float noiseHeight) {
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
	
	public float norm(float f) {
		return (1f + f) / 2f;
	}
	
    public int getCaveY(Random p_230361_1_) {
	    return p_230361_1_.nextInt(p_230361_1_.nextInt(p_230361_1_.nextInt(120 + 64) + 1) + 1) - 64;
    }

	
	protected float getWarpedNoise(int xPos, int yPos, int zPos) {
		
		if(domainWarp == null) {
			initDomainWarp();
		}
		
		Integer[] offsetsX = {-101, 71, 53, 61, 3, 13};
		//Integer[] offsetsY = {23, 29, 31, 37, 41};
		Integer[] offsetsZ = {101, 67, 59, 41, 5, 7};

		float warpX = xPos;
		float warpY = yPos;
		float warpZ = zPos;
		for(int i = 0; i < 3; i++) {
			//Not applying an offset to warpX is intentional.
			//The location for warpX can be anywhere, so it's ok that there's no offset. It hsould have no skew change or anything.
			warpX += domainWarp.GetNoise(warpX, warpY, warpZ) * 25f; //was 5 with pretty incredible results
			warpY += domainWarp.GetNoise(warpX + 20, warpY + 20, warpZ + 20) * 25f;
			warpZ += domainWarp.GetNoise(warpX - 20, warpY - 20, warpZ - 20) * 25f;
		}
		
		return this.getCaveDetailsNoise(warpX, warpY, warpZ);
	}

	//Override this for non-cavern noise
	protected float getCaveDetailsNoise(float x, float y, float z) {
		if(noise == null) {
			initNoise();
		}
		
		return noise.GetNoise(x, y, z);
	}

}
