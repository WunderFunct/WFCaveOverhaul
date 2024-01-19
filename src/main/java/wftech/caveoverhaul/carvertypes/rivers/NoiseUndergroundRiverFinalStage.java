package wftech.caveoverhaul.carvertypes.rivers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import wftech.caveoverhaul.CaveOverhaul;

public class NoiseUndergroundRiverFinalStage extends CaveWorldCarver {

	private CarvingContext ctx;
	private CaveCarverConfiguration cfg;
	private ChunkAccess level;
	private Function<BlockPos, Holder<Biome>> biome;
	private RandomSource random;
	private CarvingMask mask;
	private HashMap<String, Float> localThresholdCache;

	public NoiseUndergroundRiverFinalStage(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
	@Override
	public boolean isStartChunk(CaveCarverConfiguration p_224894_, RandomSource p_224895_) {
		return true;
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
		
		MutableBlockPos mPos = new BlockPos.MutableBlockPos();
		mPos.set(earlyXPos, 0, earlyZPos);
		
		boolean found_deepslate = false;
		for(int y = -64; y < 0; y++) {
			mPos.setY(y);
			Block blockFound = level.getBlockState(mPos).getBlock();
			if(blockFound == Blocks.DEEPSLATE) {
				found_deepslate = true;
				break;
			} else if (blockFound == Blocks.STONE) {
				break;
			}
		}
		
		List<Integer> yPosToCheck = new ArrayList<>();

		
		/*
		 * RED_STAINED_GLASS = lava
		 * GRAY_STAINED_GLASS = water
		 * YELLOW_STAINED_GLASS = stone
		 * BLACK_STAINED_GLASS = air above rivers
		 */
		int curY = 0;
		
		for(int i = 0; i < 16; i++) {
			for(int j = 0; j < 16; j++) {
				mPos.set(earlyXPos + i, 0, earlyZPos + j);

				if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer2_Lava.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer3_Lava.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer3_Water.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer3_Water.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer3_Water.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer4_Water1.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer4_Water2.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer5_Water.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer5_Water.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer5_Water.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer6_Water.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer6_Water.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer6_Water.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer7_Water.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer7_Water.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer7_Water.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}

				if(NoiseUndergroundRiver_Layer8_Water.INSTANCE.isNearbyRiver(mPos)) {
					curY = NoiseUndergroundRiver_Layer8_Water.INSTANCE.getCaveY(NoiseUndergroundRiver_Layer8_Water.INSTANCE.getCaveYNoise(earlyXPos + i, earlyZPos + j));
					runReplace(mPos, found_deepslate, curY);
				}
				
			}
		}
		
		return true;
	}

	/*
	 * RED_STAINED_GLASS = lava
	 * GRAY_STAINED_GLASS = water
	 * YELLOW_STAINED_GLASS = stone
	 * BLACK_STAINED_GLASS = air above rivers
	 */
	public void runReplace(MutableBlockPos mPos, boolean found_deepslate, int curY) {

		for(int y = -4; y <= 4; y++) {
			mPos.setY(curY + y);
			if(level.getBlockState(mPos).getBlock() == Blocks.YELLOW_STAINED_GLASS) {	
				
				if(mPos.getY() <= 0 && found_deepslate) {
					level.setBlockState(mPos, Blocks.DEEPSLATE.defaultBlockState(), false);
				} else {
					level.setBlockState(mPos, Blocks.STONE.defaultBlockState(), false);
				}
			} else if(level.getBlockState(mPos).getBlock() == Blocks.RED_STAINED_GLASS) {	
				level.setBlockState(mPos, Blocks.LAVA.defaultBlockState(), false);
				level.markPosForPostprocessing(mPos);
			} else if(level.getBlockState(mPos).getBlock() == Blocks.GRAY_STAINED_GLASS) {	
				level.setBlockState(mPos, Blocks.WATER.defaultBlockState(), false);
				level.markPosForPostprocessing(mPos);
			} else if(level.getBlockState(mPos).getBlock() == Blocks.BLACK_STAINED_GLASS) {							
				if(level.getBlockState(mPos.above()).getBlock() == Blocks.WATER) {
					if(mPos.getY() <= 0 && found_deepslate) {
						level.setBlockState(mPos, Blocks.DEEPSLATE.defaultBlockState(), false);
					} else {
						level.setBlockState(mPos, Blocks.STONE.defaultBlockState(), false);
					}
				} else {
					level.setBlockState(mPos, Blocks.AIR.defaultBlockState(), false);
				}
			}
		}
	}
}
