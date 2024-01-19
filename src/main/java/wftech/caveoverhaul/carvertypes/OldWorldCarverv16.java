package wftech.caveoverhaul.carvertypes;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import wftech.caveoverhaul.AirOnlyAquifer;

/*
 * Rename to MyceliumCarver for 0.3a
 */
public class OldWorldCarverv16 extends CaveWorldCarver {

	/*
	* Can't do literal 1.16.5- caves due to the new heights
	* With the introduction of deepslate, there's a great chance to rebalance cave densities around
	* the deepslate introduction layer. It'll create a sense of how deep the player is :)
	*/
	public OldWorldCarverv16(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
	public int getCaveY(RandomSource p_230361_1_, boolean shallow) {
		if(shallow) {
			return 130 - p_230361_1_.nextInt(p_230361_1_.nextInt(120) + 1); //130 = average y I'd like the caves to start at
		} else {
			return p_230361_1_.nextInt(p_230361_1_.nextInt(384) + 8);
		}
	}
	
	public void generateVerticalCluster(
			CarvingContext ctx, 
			CaveCarverConfiguration cfg, 
			ChunkAccess level, 
			Function<BlockPos, Holder<Biome>> pos2BiomeMapping, 
			RandomSource random, 
			Aquifer aquifer, 
			ChunkPos chunkPos, 
			CarvingMask mask, 
			int i, 
			int j, 
			boolean shallow) {

		double d0 = (double)chunkPos.getBlockX(random.nextInt(16 * 16));
		double d1 = (double) this.getCaveY(random, shallow) - (shallow ? 0 : 64);
		double d2 = (double)chunkPos.getBlockZ(random.nextInt(16 * 16));
		
		double d3 = (double)cfg.horizontalRadiusMultiplier.sample(random);
		double d4 = (double)cfg.verticalRadiusMultiplier.sample(random);
		double d5 = (double)cfg.floorLevel.sample(random);
		
		WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) -> {
			return shouldSkip(p_159203_, p_159204_, p_159205_, d5);
		};
		
		int l = 1;

		if (shallow || random.nextInt(2) == 0) {
			double d6 = (double)cfg.yScale.sample(random);
			float f1 = 1.0F + random.nextFloat() * 6.0F;
			this.createRoom(ctx, cfg, level, pos2BiomeMapping, aquifer, d0, d1, d2, f1, d6, mask, worldcarver$carveskipchecker);
			l += random.nextInt(4);
		}

		for(int k1 = 0; k1 < l; ++k1) {
			//float angle = random.nextFloat() * ((float)Math.PI * 2F);
			float angle = random.nextFloat() * ((float)Math.PI * 2F);
			float f3 = (random.nextFloat() - 0.5F) / 2.0F;; //(random.nextFloat() - 0.5F) / 2.0F;
			float f2 = this.getThickness(random); //added the +1f
			int i1 = i - random.nextInt(i / 4);
			int j1 = 0;
			this.createTunnel(ctx, 
					cfg, 
					level, 
					pos2BiomeMapping, 
					random.nextLong(), 
					aquifer, 
					d0, 
					d1, 
					d2, 
					d3, //d3
					d4, //d4
					0f, //f2
					0f, //angle
					0f, //f3
					0, 
					i1, //i
					this.getYScale(), 
					mask, 
					worldcarver$carveskipchecker);
		}
	}
		
	public void generateRoomCluster(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, Function<BlockPos, Holder<Biome>> pos2BiomeMapping, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask mask, int i, int j, boolean shallow) {

		double d0 = (double)chunkPos.getBlockX(random.nextInt(16 * 16));
		double d1 = (double)this.getCaveY(random, shallow) - (shallow ? 0 : 64);
		double d2 = (double)chunkPos.getBlockZ(random.nextInt(16 * 16));
		
		double d3 = (double)cfg.horizontalRadiusMultiplier.sample(random);
		double d4 = (double)cfg.verticalRadiusMultiplier.sample(random);
		double d5 = (double)cfg.floorLevel.sample(random);
		
		WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) -> {
			return shouldSkip(p_159203_, p_159204_, p_159205_, d5);
		};
		
		int l = 1;

		if (shallow || random.nextInt(2) == 0) {
			double d6 = (double)cfg.yScale.sample(random);
			float f1 = 1.0F + random.nextFloat() * 6.0F;
			this.createRoom(ctx, cfg, level, pos2BiomeMapping, aquifer, d0, d1, d2, f1, d6, mask, worldcarver$carveskipchecker);
			l += random.nextInt(4);
		}

		for(int k1 = 0; k1 < l; ++k1) {
			float angle = random.nextFloat() * ((float)Math.PI * 2F);
			float f3 = (random.nextFloat() - 0.5F) / 2.0F;
			float f2 = this.getThickness(random); //added the +1f
			int i1 = i - random.nextInt(i / 4);
			int j1 = 0;
			this.createTunnel(ctx, 
					cfg, 
					level, 
					pos2BiomeMapping, 
					random.nextLong(), 
					aquifer, 
					d0, 
					d1, 
					d2, 
					d3, 
					d4, 
					f2, //should be above 1 to spawn more tunnels
					angle, 
					f3, 
					0, 
					i1, //i
					this.getYScale(), 
					mask, 
					worldcarver$carveskipchecker);
		}
		
	}

	@Override
	public boolean carve(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, Function<BlockPos, Holder<Biome>> pos2BiomeMapping, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask mask) {
		int i = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
		int j = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(2, 8); // was +1 at the end
		Aquifer airAquifer = new AirOnlyAquifer(level, random.nextFloat() <=  0.3f);

		for(int k = 0; k < j; ++k) {
			//generateRoomCluster
			//generateVerticalCluster
			this.generateVerticalCluster(ctx, cfg, level, pos2BiomeMapping, random, airAquifer, chunkPos, mask, i, j, false);
		}
		
		j = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(2); // was +1 at the end
		for(int k = 0; k < j; ++k) {
			this.generateVerticalCluster(ctx, cfg, level, pos2BiomeMapping, random, airAquifer, chunkPos, mask, i, j, true);
		}

		/*
		j = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(1, 3); // was +1 at the end
		for(int k = 0; k < j; ++k) {
			this.generateRoomCluster(ctx, cfg, level, pos2BiomeMapping, random, airAquifer, chunkPos, mask, i, j, true); //moar shalow clusterszzz
		}
		*/

		return true;
	}
}
