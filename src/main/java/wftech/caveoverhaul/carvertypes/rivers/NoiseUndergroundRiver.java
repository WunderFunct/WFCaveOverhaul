package wftech.caveoverhaul.carvertypes.rivers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.utils.FabricUtils;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

public class NoiseUndergroundRiver {

	public static int MAX_CAVE_SIZE_Y = 20;
	public static float NOISE_CUTOFF_RIVER = 0.92f;
	public static FastNoiseLite domainWarp = null;
	public static FastNoiseLite noise = null;
	public static FastNoiseLite noiseShouldCarveBase = null;
	public static FastNoiseLite noiseYLevelBase = null;

	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};

	protected void initNoise() {
		
		if(noise != null) {
			return;
		}		

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenSettings().seed());
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
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenSettings().seed() + 51);
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
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenSettings().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.025f);
		tnoise.SetFractalLacunarity(1.1f);
		tnoise.SetFractalGain(1.6f);
		domainWarp = tnoise;
	}
	
	protected void initShouldCarveNoise() {
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) FabricUtils.server.getWorldData().worldGenSettings().seed() + 50);
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

	protected float getCaveDetailsNoise2D(float x, float z) {
		if(noise == null) {
			initNoise();
		}
		
		return noise.GetNoise(x, z);
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
