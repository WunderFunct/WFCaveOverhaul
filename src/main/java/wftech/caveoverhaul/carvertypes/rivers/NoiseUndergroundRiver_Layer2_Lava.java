package wftech.caveoverhaul.carvertypes.rivers;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.utils.FabricUtils;

public class NoiseUndergroundRiver_Layer2_Lava extends NoiseUndergroundRiver {

	public static int MAX_CAVE_SIZE_Y = 20;
	public static float NOISE_CUTOFF_RIVER = 0.92f;
	private int seedOffset = 59;

	public static FastNoiseLite mNoise = null;
	public static FastNoiseLite mNoiseShouldCarveBase = null;
	public static FastNoiseLite mNoiseYLevelBase = null;

	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};

	public static NoiseUndergroundRiver INSTANCE = new NoiseUndergroundRiver_Layer2_Lava();
	public NoiseUndergroundRiver_Layer2_Lava() {
		super();
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
	
	@Override
	protected int getCaveY(float noiseValue) {
		//40 is the midpoint
		float min = -42;
		float max = (-42) + 8; //4
		if(!CaveOverhaul.ENABLE_MULTILAYER_RIVERS){
			return (int) min;
		}
		float diffSize = max - min;
		return (int) (noiseValue * (diffSize)) + (int) min;
	}
	
	@Override
	protected Block getLiquidType() {
		return Blocks.LAVA;
	}
	
	//Lava by default (in a mixed set) -OR- <entry>2 -> > 0f. Else, < 0f.
	@Override
	protected boolean isOutOfBounds(int x, int z) {
		float shouldCarveNoise = this.getShouldCarveNoise(x, z);
		return shouldCarveNoise < 0f;
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
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenOptions().seed() + seedOffset + 2);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.003f); //CHANGED was 0.003
		tnoise.SetFractalType(FractalType.Ridged);
		tnoise.SetFractalOctaves(1);
		
		mNoise = tnoise;
	}
	
	@Override
	protected void initNoiseYLevel() {

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenOptions().seed() + seedOffset);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFrequency(0.002f);
				
		mNoiseYLevelBase = tnoise;
	}
	
	@Override
	protected void initShouldCarveNoise() {
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenOptions().seed() + seedOffset + 1);
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
