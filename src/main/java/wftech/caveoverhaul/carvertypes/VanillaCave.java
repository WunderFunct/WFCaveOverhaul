package wftech.caveoverhaul.carvertypes;

import java.util.Random;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import wftech.caveoverhaul.AirOnlyAquifer;

public class VanillaCave extends CaveWorldCarver {

	public VanillaCave(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean carve(
			CarvingContext p_224885_, 
			CaveCarverConfiguration p_224886_, 
			ChunkAccess chunkAccess, 
			Function<BlockPos, Holder<Biome>> p_224888_, 
			Random p_224889_, 
			Aquifer aquifer, 
			ChunkPos p_224891_, 
			CarvingMask p_224892_) {
		
		return super.carve(p_224885_, p_224886_, chunkAccess, p_224888_, p_224889_, new AirOnlyAquifer(chunkAccess, p_224889_.nextFloat() <= 0.3f), p_224891_, p_224892_);
	}

}
