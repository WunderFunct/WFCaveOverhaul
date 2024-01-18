package wftech.caveoverhaul.carvertypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

/*
 * Core concept: skip carving
 * 
 * This carver uses a "skip carver," where every nth block is checked.
 * The idea is that no meaningful change can occur within, say, 3 blocks
 * so if every 3rd block is checked and no change in state is found from 
 * the previous check, you can fill the skipped blocks in with the state of
 * both checked blocks. Likewise, if a change is found, then a state change
 * between air and stone has occurred locally within the y column, so you 
 * only check nearby (y) blocks that would have normally have been skipped.
 * That is:
 * 1. Store the state of the current y block
 * 2. Advance the y cursor by 3 positions.
 * 3. Check the state of the new y block. If the same, fill in the same block
 * 		for the previous 2 positions. Else, check the previous 2 and next 2
 * 		blocks for their true value and log each block's state.
 * 4. Repeat from (1).
 * 
 * By doing this, the number of actual noise checks should drop by a large amount,
 * thus dramatically speeding up the carving process.
 */
public abstract class NoiseCavernBaseSkipCarverVariantMT extends CaveWorldCarver {

	public static int MAX_CAVE_SIZE_Y = 20;
	public static int SKIP_AMOUNT = 2;
	public static int NUM_THREADS = 16;
	
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
	
	public NoiseCavernBaseSkipCarverVariantMT(Codec<CaveCarverConfiguration> p_159194_) {
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
		tnoise.SetFractalGain(0.3f);
		tnoise.SetFrequency(0.025f);
		tnoise.SetFractalType(FractalType.FBm);
		
		
		/*
		 * Too carvernous, not going to use
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		//tnoise.SetFractalType(FractalType.FBM);
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(2.5f);
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.1f);
		*/

		/*
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFractalGain(0.3f); //seems to top out at 3.5 though
		tnoise.SetFrequency(0.025f); //was 0.01
		tnoise.SetFractalType(FractalType.FBm);
		*/	
		
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
		tnoise.SetFractalGain(0.3f);
		tnoise.SetFrequency(0.025f);
		tnoise.SetFractalType(FractalType.FBm);
		
		
		/*
		 * Too carvernous, not going to use
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		//tnoise.SetFractalType(FractalType.FBM);
		tnoise.SetFractalType(FractalType.FBm);
		tnoise.SetFractalGain(2.5f);
		tnoise.SetFractalOctaves(2);
		tnoise.SetFractalLacunarity(0.1f);
		*/

		/*
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetFractalOctaves(1);
		tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
		tnoise.SetFractalGain(0.3f); //seems to top out at 3.5 though
		tnoise.SetFrequency(0.025f); //was 0.01
		tnoise.SetFractalType(FractalType.FBm);
		*/	
		
		noise = tnoise;
	}
	
	
	protected int getCaveY(float noiseValue) {
		return (int) (noiseValue * (64f));
	}
	
	public void initDomainWarp() {
		
		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed());
		tnoise.SetNoiseType(NoiseType.OpenSimplex2);
		tnoise.SetFrequency(0.01f);
		domainWarp = tnoise;
	}
	
	public static void initDomainWarpStatic() {
		
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

		if(this.domainWarp == null) {
			this.initDomainWarp();
		}
		if(this.noise == null) {
			this.initNoise();
		}

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
		
		List<BlockPos> airPositions = new ArrayList<>();
		List<Future<List<BlockPos>>> futures = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

		airPositions = new ArrayList<>();
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
				//CHANGED
				//caveY = 4;
				//caveHeight = 128;
				
				mPos.set(earlyXPos, 0, earlyZPos);				
				
				futures.add(executor.submit(new GenAirPocketLocationsTask(mPos, caveY, caveHeight)));
			}
		}

        for (Future<List<BlockPos>> future : futures) {
            try {
            	airPositions.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
        }
		executor.shutdown();
        
		//Air positions actual carve
		for(BlockPos blockPos: airPositions) {

			try {
				boolean setToLiquid = this.carveBlock(ctx, cfg, level, pos2BiomeMapping, mask, blockPos.mutable(), unkPos, aquifer, mBool);
				if(!setToLiquid) {
					mask.set(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				}
				LevelAccessor access = level.getWorldForge();
			} catch (ArrayIndexOutOfBoundsException e){
				CaveOverhaul.LOGGER.error("[Cave Overhaul] NoiseCarverTest real error");
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
	
	public float norm(float f) {
		return (1f + f) / 2f;
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
	
    public int getCaveY(Random p_230361_1_) {
	    return p_230361_1_.nextInt(p_230361_1_.nextInt(p_230361_1_.nextInt(120 + 64) + 1) + 1) - 64;
    }


    static class GenAirPocketLocationsTask implements Callable<List<BlockPos>> {
    	private BlockPos basePos;
    	private int caveY;
    	private int caveHeight;
    	
    	public GenAirPocketLocationsTask(BlockPos basePos, int caveY, int caveHeight) {
    		this.basePos = basePos;
    		this.caveY = caveY;
    		this.caveHeight = caveHeight;
    	}
    	
        @Override
        public List<BlockPos> call() throws Exception {
        	MutableBlockPos tPos = new MutableBlockPos();
        	tPos.set(basePos.getX(), basePos.getY(), basePos.getZ());
            List<BlockPos> positions = this.generateAirPositionsUsingSkipCarving(tPos, this.caveY, this.caveHeight);
            
            return positions;
        }
        
    	protected List<BlockPos> generateAirPositionsUsingSkipCarving(MutableBlockPos mPos, int caveY, int caveHeight){
    		List<BlockPos> airPositions = new ArrayList<>();
    		
    		boolean carvePrevPosition = false;
    		boolean firstCheck = true;
    		int y_adj = 300;
    		for(int y_unadj = caveY + caveHeight; y_unadj > caveY; y_unadj -= SKIP_AMOUNT) {
    			y_adj = y_unadj;
    			int yPos = y_unadj;

    			if(y_adj <= -64) {
    				CaveOverhaul.LOGGER.error("[Cave Overhaul] NoiseCarverTest below -64!");
    				continue;
    			}
    			
    			float noiseFound = this.getWarpedNoise(mPos.getX(), yPos*2, mPos.getZ());
    			boolean shouldCarve = noiseFound > this.getNoiseThreshold(mPos.getX(), mPos.getZ()); //noiseFound > this.getNoiseThreshold(xPos, zPos); //was 0.08
    			
    			if(shouldCarve) {
    				if(firstCheck) {
    					//32; 30; 34
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y > y_adj - SKIP_AMOUNT; t_y--) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y < y_adj + SKIP_AMOUNT; t_y++) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    					firstCheck = false;
    				} else if(!carvePrevPosition) {
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y > y_adj - SKIP_AMOUNT; t_y--) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y < y_adj + SKIP_AMOUNT; t_y++) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    				} else {
    					//Just fill in missing air pockets
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y < y_adj + SKIP_AMOUNT; t_y++) {
    						airPositions.add(mPos.setY(t_y).immutable());
    					}
    				}
    				carvePrevPosition = true;
    				airPositions.add(mPos.setY(y_adj).immutable());
    			} else {
    				if(firstCheck) {
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y > y_adj - SKIP_AMOUNT; t_y--) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y < y_adj + SKIP_AMOUNT; t_y++) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    					firstCheck = false;
    				} else if(carvePrevPosition) {
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y > y_adj - SKIP_AMOUNT; t_y--) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    					for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y < y_adj + SKIP_AMOUNT; t_y++) {
    						mPos.setY(t_y);
    						float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    						boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    						if(shouldCarveSC) {
    							airPositions.add(mPos.setY(t_y).immutable());
    						}
    					}
    				} else {
    					//do nothing as it's a stone to stone span
    				}
    				carvePrevPosition = false;
    			}
    		}
    		
    		if(y_adj < 300) {
    			for (int t_y = y_adj; t_y > -64 && t_y < 320 && t_y < y_adj + SKIP_AMOUNT; t_y++) {
    				mPos.setY(t_y);
    				float noiseFoundSC = this.getWarpedNoise(mPos.getX(), mPos.getY()*2, mPos.getZ());
    				boolean shouldCarveSC = noiseFoundSC > this.getNoiseThreshold(mPos.getX(), mPos.getZ());
    				if(shouldCarveSC) {
    					airPositions.add(mPos.setY(t_y).immutable());
    				}
    			}
    		}
    		
    		return airPositions;
    	}
    	

    	protected float getWarpedNoise(int xPos, int yPos, int zPos) {

    		//CHANGED, was true
    		if(yPos >= 0)
    		{
    			return this.getCaveDetailsNoise(xPos, yPos, zPos);
    		}
    			
    		if(domainWarp == null) {
    			NoiseCavernBaseSkipCarverVariantMT.initDomainWarpStatic();
    		}
    		
    		Integer[] offsetsX = {-101, 71, 53, 61, 3, 13};
    		//Integer[] offsetsY = {23, 29, 31, 37, 41};
    		Integer[] offsetsZ = {101, 67, 59, 41, 5, 7};

    		float warpSlide = 25f * ( -yPos / 64f);
    		float yOrig = yPos / 2f;
    		float yAdjPart = ( -yPos / 64f);
    		float yAdj = 2f - yAdjPart;
    		
    		float warpX = xPos;
    		float warpY = yPos;
    		float warpZ = zPos;
    		//warpY = yAdj;
    		for(int i = 0; i < 3; i++) {
    			//CHANGED
    			//Not applying an offset to warpX is intentional.
    			//The location for warpX can be anywhere, so it's ok that there's no offset. It hsould have no skew change or anything.
    			warpX += domainWarp.GetNoise(warpX, warpY, warpZ) * 25f; //was 5 with pretty incredible results
    			warpY += domainWarp.GetNoise(warpX + 20, warpY + 20, warpZ + 20) * warpSlide;
    			warpZ += domainWarp.GetNoise(warpX - 20, warpY - 20, warpZ - 20) * warpSlide;
    		}
    		
    		return this.getCaveDetailsNoise(warpX, warpY, warpZ);
    	}

    	//Override this for non-cavern noise
    	protected float getCaveDetailsNoise(float x, float y, float z) {
    		if(noise == null) {
    			NoiseCavernBaseSkipCarverVariantMT.initNoiseStatic();
    		}
    		
    		return noise.GetNoise(x, y, z);
    	}
    	
    	protected float getNoiseThreshold(float x, float z) {
    		//default 0.08
    		return 0.15f;
    	}
    }

}
