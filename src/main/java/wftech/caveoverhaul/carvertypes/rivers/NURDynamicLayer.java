package wftech.caveoverhaul.carvertypes.rivers;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;

import java.util.HashMap;
import java.util.function.Function;

public class NURDynamicLayer extends NoiseUndergroundRiver {

	public static int MAX_CAVE_SIZE_Y = 20;
	public static float NOISE_CUTOFF_RIVER = 0.92f;
	public int seedOffset = 82;
	private int min_y = 0;
	private Block blockType = Blocks.WATER;

	//public static FastNoiseLite noise = null;
	//public static FastNoiseLite yNoise = null;
	//public static FastNoiseLite caveSizeNoise = null;
	public static FastNoiseLite mNoise = null;
	public static FastNoiseLite mNoiseShouldCarveBase = null;
	public static FastNoiseLite mNoiseYLevelBase = null;

	private CarvingContext ctx;
	private CaveCarverConfiguration cfg;
	private ChunkAccess level;
	private Function<BlockPos, Holder<Biome>> biome;
	private RandomSource random;
	private Aquifer aquifer;
	private CarvingMask mask;
	private HashMap<String, Float> localThresholdCache;

	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};

	//public static NoiseUndergroundRiver INSTANCE = new NURDynamicLayer();
	public NURDynamicLayer(Block blockType, int min_y, int seedOffset) {
		super();
		this.blockType = blockType;
		this.min_y = min_y;
		this.seedOffset = seedOffset;
	}

	public NURDynamicLayer(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
		// TODO Auto-generated constructor stub
	}
	/*
	 * Edit code below
	 * 
	 * Layer 1 = -64 to -48
	 * 2 = -48 to -32 * Lava x1
	 * 3 = -32 to -16 * Mixed
	 * -16 to 0 * Water x2
	 * 0 to 16 * Water x1
	 * 16 to 32 * Water x1
	 * 32 to 48 * Water x1
	 * 48 to 64
	 */

	public void setSeed(int seed){
		this.seedOffset = seed;
	}

	public void set_min_y(int min_y){
		this.min_y = min_y;
	}

	public void setBlockType(Block block){
		this.blockType = block;
	}
	
	@Override
	protected int getCaveY(float noiseValue) {
		//40 is the midpoint
		float min = this.min_y;
		float max = (this.min_y) + 8; //3

		if (this.blockType == Blocks.WATER) {
			if(Config.getBoolSetting(Config.KEY_WATER_RIVER_FLAT)){
				return (int) min;
			}
		} else if (this.blockType == Blocks.LAVA) {
			if(Config.getBoolSetting(Config.KEY_LAVA_RIVER_FLAT)){
				return (int) min;
			}
		}

		float diffSize = max - min;
		return (int) (noiseValue * (diffSize)) + (int) min;
	}
	
	@Override
	protected Block getLiquidType() {
		return this.blockType;
	}
	
	//Lava by default (in a mixed set) -OR- <entry>2 -> > 0f. Else, < 0f.
	@Override
	protected boolean isOutOfBounds(int x, int z) {
		float shouldCarveNoise = this.getShouldCarveNoise(x, z);
		return shouldCarveNoise > 0f;
	}

	/*
	 * Don't edit code below
	 */

	@Override
	protected float getCaveDetailsNoise2D(float x, float z) {
		if(mNoise == null) {
			initNoise();
		}
		
		return mNoise.GetNoise(x, z);
	}
	
	@Override
	protected void initNoise() {		

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + seedOffset + 2);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.003f); //CHANGED was 0.003
		tnoise.SetFractalType(FractalType.Ridged);
		tnoise.SetFractalOctaves(1);
		
		mNoise = tnoise;
	}
	
	@Override
	protected void initNoiseYLevel() {

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + seedOffset);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.002f);
				
		mNoiseYLevelBase = tnoise;
	}
	
	@Override
	protected void initShouldCarveNoise() {
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + seedOffset + 1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.0015f);
		mNoiseShouldCarveBase = tnoise;
	}
	
	@Override
	protected float getCaveYNoise(int x, int z) {
		if(mNoiseYLevelBase == null) {
			initNoiseYLevel();
		}
		
		return mNoiseYLevelBase.GetNoise(x, z);
	}
	
	@Override
	protected float getShouldCarveNoise(int x, int z) {
		if(mNoiseShouldCarveBase == null) {
			initShouldCarveNoise();
		}
		
		return mNoiseShouldCarveBase.GetNoise(x, z);
	}
	
}
