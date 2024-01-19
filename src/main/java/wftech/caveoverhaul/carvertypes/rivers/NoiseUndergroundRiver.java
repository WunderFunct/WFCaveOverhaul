package wftech.caveoverhaul.carvertypes.rivers;

import java.util.HashMap;
import java.util.Random;

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
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
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
import wftech.caveoverhaul.DummyCodec;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.DomainWarpType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

public class NoiseUndergroundRiver extends CaveWorldCarver {

	public static int MAX_CAVE_SIZE_Y = 20;
	public static float NOISE_CUTOFF_RIVER = 0.92f;
	public static Block SAFE_ADD_BLOCK = Blocks.YELLOW_STAINED_GLASS;
	
	//public static FastNoiseLite noise = null;
	//public static FastNoiseLite yNoise = null;
	//public static FastNoiseLite caveSizeNoise = null;
	public static FastNoiseLite domainWarp = null;
	public static FastNoiseLite noise = null;
	public static FastNoiseLite noiseShouldCarveBase = null;
	public static FastNoiseLite noiseYLevelBase = null;
	
	private CarvingContext ctx;
	private CaveCarverConfiguration cfg;
	private ChunkAccess level;
	private Function<BlockPos, Holder<Biome>> biome;
	private RandomSource random;
	private Aquifer aquifer;
	private CarvingMask mask;
	private HashMap<String, Float> localThresholdCache;
	
	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};
	
	public NoiseUndergroundRiver() {
		super(new DummyCodec());
	}
	
	public NoiseUndergroundRiver(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
		// TODO Auto-generated constructor stub
	}
	
	//abstract float getCaveYNoise(float x, float z);
	//abstract float getCaveThicknessNoise(float x, float z);
	
	@Override
	public boolean isStartChunk(CaveCarverConfiguration p_224894_, RandomSource random) {
		return true;
	}
	
	protected void initNoise() {
		
		if(noise != null) {
			return;
		}		

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.003f); //CHANGED was 0.003
		tnoise.SetFractalType(FractalType.Ridged);
		//Fractal gain = 0.2, but I think it does nothing for this use case
		tnoise.SetFractalOctaves(1);
		
		/*
		tnoise.SetDomainWarpType(DomainWarpType.OpenSimplex2);
		tnoise.SetDomainWarpAmp(75f);
		tnoise.SetDomainWarpGain(0.4f);
		tnoise.SetDomainWarpOctaves(2);
		tnoise.SetDomainWarpLacunarity(2.7f);
		tnoise.SetDomainWarpFrequency(0.018f);
		*/
		
		/*
		 * Is domain warp broken? WTF?
		 * ^ Yup. It's not true domain warp. Time to use my own solution.
		 */
		/*
		tnoise.SetDomainWarpType(DomainWarpType.OpenSimplex2);
		tnoise.SetDomainWarpAmp(75f); //lowered from 75f to 35f //CHANGED
		//noise.SetDomainWarpGain(0.4f);
		//tnoise.SetDomainWarpOctaves(2);
		tnoise.SetDomainWarpLacunarity(2.7f);
		tnoise.SetDomainWarpFrequency(0.045f); //increased to 0.045 //0.018 is minimum for labarynthine canals //CHANGED
		*/
				
		noise = tnoise;
	}
	
	protected void initNoiseYLevel() {
		
		if(noise != null) {
			return;
		}		

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + 51);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.002f);
				
		noiseYLevelBase = tnoise;
	}
	
	
	protected int getCaveY(float noiseValue) {
		float min = -3;
		float max = 0;
		float diffSize = max - min;
		return (int) (noiseValue * (diffSize)) + (int) min;
	}
	
	protected void initDomainWarp() {
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.025f);
		tnoise.SetFractalLacunarity(1.1f);
		tnoise.SetFractalGain(1.6f);
		domainWarp = tnoise;
	}
	
	protected void initShouldCarveNoise() {
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + 50);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.0015f);
		noiseShouldCarveBase = tnoise;
	}
	
	protected float getShouldCarveNoise(int x, int z) {
		if(noiseShouldCarveBase == null) {
			initShouldCarveNoise();
		}
		
		return noiseShouldCarveBase.GetNoise(x, z);
	}
	
	protected float getCaveYNoise(int x, int z) {
		if(noiseYLevelBase == null) {
			initNoiseYLevel();
		}
		
		return noiseYLevelBase.GetNoise(x, z);
	}
	
	protected Block getLiquidType() {
		return Blocks.WATER;
	}
	
	protected boolean isOutOfBounds(int x, int z) {
		float shouldCarveNoise = this.getShouldCarveNoise(x, z);
		return shouldCarveNoise < 0f;
	}
	
	@Override
	public boolean carve(
		CarvingContext ctx, 
		CaveCarverConfiguration cfg, 
		ChunkAccess level, 
		Function<BlockPos, Holder<Biome>> pos2BiomeMapping, 
		RandomSource random, 
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

		ChunkPos chunkPos = level.getPos();
		int earlyXPos = chunkPos.getBlockX(0);
		int earlyZPos = chunkPos.getBlockZ(0);
		
		//Random _filterRandom = new Random((String.valueOf(earlyXPos) + String.valueOf(earlyZPos)).hashCode());
		
		//float nFloat = _filterRandom.nextFloat();
		
		//if(nFloat <= NOISE_CUTOFF_RIVER) {
		//	return false;
		//}
		
		Aquifer airAquifer = new AirOnlyAquifer(level, false); //random.nextFloat() <= Config.PERC_PIERCE_SURFACE.get()
		this.aquifer = airAquifer;
		//this.aquifer = _aquifer;
		
		BlockPos _basePos = chunkPos.getWorldPosition();

		MutableBlockPos mPos = new BlockPos.MutableBlockPos();
		MutableBlockPos chunkCenter = new BlockPos.MutableBlockPos();
		MutableBlockPos unkPos = new BlockPos.MutableBlockPos();
		MutableBoolean mBool = new MutableBoolean();
		
		
		for(int x_offset = 0; x_offset < 16; x_offset++) {
			for(int z_offset = 0; z_offset < 16; z_offset++) {
				float yLevelNoise = this.getCaveYNoise(earlyXPos, earlyZPos);
				int y = this.getCaveY(yLevelNoise);
				BlockPos bPos = new BlockPos(earlyXPos + x_offset, y, earlyZPos + z_offset);
				
				Block preferredBlock = this.getLiquidType();
				
				if(level.getBlockState(bPos).getBlock() == preferredBlock) {
					return true;
				}
				
				if(this.level.getHeight(Types.WORLD_SURFACE_WG, bPos.getX(), bPos.getZ()) <= (bPos.getY() - 12)) {
					continue;
				}
				
				if(this.level.getHeight(Types.OCEAN_FLOOR_WG, bPos.getX(), bPos.getZ()) <= (bPos.getY() - 12)) {
					continue;
				}
				
				if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
					continue;
				}

				if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.5){
					continue;
				}
				
				//float noise = this.getNoise2D(earlyXPos + x_offset, earlyZPos + z_offset);
				float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
				
				
				//Begin!
				if(noise > NOISE_CUTOFF_RIVER) {
					this.recursiveDig(earlyXPos, earlyZPos, bPos, preferredBlock, 0, new Vector2f(), false, level, noise);
				} else if (noise > NOISE_CUTOFF_RIVER - 0.05){

					MutableBlockPos mbPos = new MutableBlockPos();
					mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ());
					mbPos.set(bPos.getX() + 1, bPos.getY(), bPos.getZ());
					if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
						
						float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
						int y1 = this.getCaveY(yLevelNoise);
						BlockPos adjPos = new BlockPos(bPos.getX(), y1, bPos.getZ());
						Block replacementBlock = SAFE_ADD_BLOCK;
						level.setBlockState(adjPos, replacementBlock.defaultBlockState(), false);
					}
					mbPos.set(bPos.getX() - 1, bPos.getY(), bPos.getZ());
					if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
						float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
						int y1 = this.getCaveY(yLevelNoise);
						BlockPos adjPos = new BlockPos(bPos.getX(), y1, bPos.getZ());
						Block replacementBlock = SAFE_ADD_BLOCK;
						level.setBlockState(adjPos, replacementBlock.defaultBlockState(), false);
					}
					mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() + 1);
					if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
						float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
						int y1 = this.getCaveY(yLevelNoise);
						BlockPos adjPos = new BlockPos(bPos.getX(), y1, bPos.getZ());
						Block replacementBlock = SAFE_ADD_BLOCK;
						level.setBlockState(adjPos, replacementBlock.defaultBlockState(), false);
					}
					mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() - 1);
					if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
						float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
						int y1 = this.getCaveY(yLevelNoise);
						BlockPos adjPos = new BlockPos(bPos.getX(), y1, bPos.getZ());
						Block replacementBlock = SAFE_ADD_BLOCK;
						level.setBlockState(adjPos, replacementBlock.defaultBlockState(), false);
					}
				}
				
			}
		}
		
		return true;
	}
	
	
	protected void recursiveDig(int earlyX, int earlyZ, 
			BlockPos curPos, Block placementBlock, 
			int placementsDone, Vector2f momentumVector, boolean lockMomentum, ChunkAccess level, float rawNoise) {
		if(placementsDone > 10) {
			return;
		}
		
		if(level.getBlockState(curPos).getBlock() == placementBlock) {
			return;
		}

		//carve floor
		level.setBlockState(curPos, placementBlock.defaultBlockState(), false);
		level.markPosForPostprocessing(curPos);
		level.setBlockState(curPos.above(1), Blocks.AIR.defaultBlockState(), false);
		level.setBlockState(curPos.above(2), Blocks.AIR.defaultBlockState(), false);
		
		//Carve roof
		float noiseDelta = rawNoise - NOISE_CUTOFF_RIVER;
		int noiseCutoffCeiling = (int) (noiseDelta * 100);
		noiseCutoffCeiling /= 2;
		int topDelta = noiseCutoffCeiling + 2;
		for(int i = 1; i < noiseCutoffCeiling; i++) {
			level.setBlockState(curPos.above(2 + i), Blocks.AIR.defaultBlockState(), false);
		}
		
		//add supporting blocks below
		BlockPos topPos = curPos.above(topDelta);
		boolean topIsLiquid = level.getBlockState(topPos).liquid();
		if(topIsLiquid) {
			level.setBlockState(topPos, SAFE_ADD_BLOCK.defaultBlockState(), false);
		}
		
		//Another layer even deeper below
		Block replacementBlock = SAFE_ADD_BLOCK;
		level.setBlockState(curPos.below(), replacementBlock.defaultBlockState(), false);

		replacementBlock = SAFE_ADD_BLOCK;
		boolean carveBelow = level.getBlockState(curPos.below(2)).isAir();
		level.setBlockState(curPos.below(2), replacementBlock.defaultBlockState(), false);

		
	}
	
	protected boolean shouldAdjustY() {
		return true;
	}

	protected float getNoiseThreshold(float x, float z) {
		//default 0.08
		return 0.08f;
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
	
    public int getCaveY(RandomSource p_230361_1_) {
	    return p_230361_1_.nextInt(p_230361_1_.nextInt(p_230361_1_.nextInt(120 + 64) + 1) + 1) - 64;
    }

	
	protected float getWarpedNoise(int xPos, int zPos) {
			
		if(domainWarp == null) {
			initDomainWarp();
		}
		
		Integer[] offsetsX = {-101, 71, 53, 61, 3, 13};
		//Integer[] offsetsY = {23, 29, 31, 37, 41};
		Integer[] offsetsZ = {101, 67, 59, 41, 5, 7};

		float warpX = xPos;
		float warpZ = zPos;
		for(int i = 0; i < 2; i++) {
			//CHANGED
			//Not applying an offset to warpX is intentional.
			//The location for warpX can be anywhere, so it's ok that there's no offset. It hsould have no skew change or anything.
			warpX += domainWarp.GetNoise(warpX + 20, warpZ + 20) * 2f; //was 5 with pretty incredible results
			warpZ += domainWarp.GetNoise(warpX - 20, warpZ - 20) * 2f;
		}
		
		return this.getNoise2D((int) warpX, (int) warpZ);
	}
	
	public float getNoise2D(int xPos, int zPos) {
		return this.getCaveDetailsNoise2D(xPos, zPos);
	}
	
	public float getNoise2D(BlockPos pos) {
		return this.getCaveDetailsNoise2D(pos.getX(), pos.getZ());
	}

	public float getCaveDetailsNoise(float x, float y, float z) {
		if(noise == null) {
			initNoise();
		}
		
		return noise.GetNoise(x, y, z);
	}

	protected float getCaveDetailsNoise2D(float x, float z) {
		if(noise == null) {
			initNoise();
		}
		
		return noise.GetNoise(x, z);
	}

	public boolean isNearbyRiver(int x, int y, int z) {
		return isNearbyRiver(new BlockPos(x, y, z));
	}
	
	public boolean isNearbyRiver(BlockPos bPos) {
		if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
			return false;
		}

		if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.75){
			return false;
		}
		
		return true;
	}

	
	/*
	 * Do not edit below
	 */
	public boolean isLava(int x, int y, int z) {
		Block preferredBlock = this.getLiquidType();
		
		if(preferredBlock != Blocks.LAVA) {
			return false;
		}
		
		BlockPos bPos = new BlockPos(x, y, z);
		
		if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
			return false;
		}

		if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.75){
			return false;
		}

		float yLevelNoise = this.getCaveYNoise(x, z);
		int caveY = this.getCaveY(yLevelNoise);
		if(caveY != y) {
			return false;
		}
		
		float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
		if(noise > NOISE_CUTOFF_RIVER) {
			return true;
		}
		
		return false;
	}
	
	public boolean isWater(int x, int y, int z) {
		Block preferredBlock = this.getLiquidType();
		
		if(preferredBlock != Blocks.WATER) {
			return false;
		}
		
		BlockPos bPos = new BlockPos(x, y, z);
		
		if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
			return false;
		}

		if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.75){
			return false;
		}

		float yLevelNoise = this.getCaveYNoise(x, z);
		int caveY = this.getCaveY(yLevelNoise);
		if(caveY != y) {
			return false;
		}
		
		float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
		if(noise > NOISE_CUTOFF_RIVER) {
			return true;
		}
		
		return false;
	}
	
	//checkIfInRiver = true for the noise mixin, false = if it's called by waterfall function
	public boolean isBoundary(int x, int y, int z, boolean checkIfInRiver) {
		BlockPos bPos = new BlockPos(x, y, z);
		
		if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
			return false;
		}

		//was 0.75
		if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.75){
			return false;
		}
		
		float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
		boolean shouldCarveRiver = noise > NOISE_CUTOFF_RIVER;
		if(shouldCarveRiver) {
			return false;
		}

		//float yLevelNoise_o = this.getCaveYNoise(x, z);
		//int y_o = this.getCaveY(yLevelNoise_o);
		
		/*
		MutableBlockPos mbPos = new MutableBlockPos();
		mbPos.set(bPos.getX() + 1, bPos.getY(), bPos.getZ());
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int y1 = this.getCaveY(yLevelNoise1);
			return y == y1;
		}
		mbPos.set(bPos.getX() - 1, bPos.getY(), bPos.getZ());
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int y1 = this.getCaveY(yLevelNoise1);
			return y == y1;
		}
		mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() + 1);
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int y1 = this.getCaveY(yLevelNoise1);
			return y == y1;
		}
		mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() - 1);
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int y1 = this.getCaveY(yLevelNoise1);
			return y == y1;
		}
		*/
		
		boolean shouldCheckBoundary = true;
		for(int i = 0; i < 5; i++) {
			if(shouldCheckBoundary) {
				shouldCheckBoundary = !NoiseChunkMixinUtils.shouldSetToLava(128, x, y - i, z) && 
						!NoiseChunkMixinUtils.shouldSetToWater(128, x, y - i, z);
			}
		}
		
		// /tp 4383 -18 3784
		if(shouldCheckBoundary) {
			if(NoiseChunkMixinUtils.shouldSetToLava(128, x + 1, y, z) || NoiseChunkMixinUtils.shouldSetToWater(128, x + 1, y, z)) {
				return true;
			} else if(NoiseChunkMixinUtils.shouldSetToLava(128, x - 1, y, z) || NoiseChunkMixinUtils.shouldSetToWater(128, x - 1, y, z)) {
				return true;
			} else if(NoiseChunkMixinUtils.shouldSetToLava(128, x, y, z + 1) || NoiseChunkMixinUtils.shouldSetToWater(128, x, y, z + 1)) {
				return true;
			} else if(NoiseChunkMixinUtils.shouldSetToLava(128, x, y, z - 1) || NoiseChunkMixinUtils.shouldSetToWater(128, x, y, z - 1)) {
				return true;
			}
		}
		
		return false;
	}

	//checkIfInRiver = true for the noise mixin, false = if it's called by waterfall function
	public boolean isBelowWaterfallSupport(int x, int y, int z) {
		BlockPos bPos = new BlockPos(x, y, z);
		
		if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
			return false;
		}

		if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.75){
			return false;
		}
		
		float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
		boolean noiseAboveCutoff = noise > NOISE_CUTOFF_RIVER;

		float yLevelNoise_o = this.getCaveYNoise(x, z);
		int y_o = this.getCaveY(yLevelNoise_o);
		
		MutableBlockPos mbPos = new MutableBlockPos();
		mbPos.set(bPos.getX() + 1, bPos.getY(), bPos.getZ());
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int neighborY = this.getCaveY(yLevelNoise1);
			return y == neighborY && (y_o != y) && neighborY < y_o;
		}
		mbPos.set(bPos.getX() - 1, bPos.getY(), bPos.getZ());
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int neighborY = this.getCaveY(yLevelNoise1);
			return y ==neighborY && y_o != y && neighborY < y_o;
		}
		mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() + 1);
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int neighborY = this.getCaveY(yLevelNoise1);
			return y == neighborY && y_o != y && neighborY < y_o;
		}
		mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() - 1);
		if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
			float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
			int neighborY = this.getCaveY(yLevelNoise1);
			return y == neighborY && y_o != y && neighborY < y_o;
		}
		
		return false;
	}

	
	public boolean isBelowRiverSupport(int x, int y, int z) {
		if(this.getLiquidType() == Blocks.LAVA) {
			if(this.isLava(x, y + 1, z)) {
				return true;
			}
			if(this.isLava(x, y + 2, z)) {
				return true;
			}
		} else {
			if(this.isWater(x, y + 1, z)) {
				return true;
			}
			if(this.isWater(x, y + 2, z)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isAir(int x, int y, int z) {
		BlockPos bPos = new BlockPos(x, y, z);
		
		if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
			return false;
		}

		if(this.getNoise2D(bPos.getX(), bPos.getZ()) < 0.75){
			return false;
		}
		
		float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
		if(noise <= NOISE_CUTOFF_RIVER) {
			return false;
		}
		
		if(isRiver(x, y - 1, z)) {
			return true;
		} else if(isRiver(x, y - 2, z)) {
			return true;
		}

		float noiseDiff = noise - NOISE_CUTOFF_RIVER;
		
		//Carve roof
		float noiseDelta = noiseDiff;
		int noiseCutoffCeiling = (int) (noiseDelta * 100);
		noiseCutoffCeiling /= 2;
		int topDelta = noiseCutoffCeiling + 2;
		for(int i = 1; i < noiseCutoffCeiling; i++) {
			//level.setBlockState(curPos.above(2 + i), Blocks.AIR.defaultBlockState(), false);
			if(isRiver(x, y - (2 + i), z)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean isRiver(int x, int y, int z) {
		if(this.getLiquidType() == Blocks.LAVA) {
			return this.isLava(x, y, z);
		} else {
			return this.isWater(x, y, z);
		}
	}
}
