package wftech.caveoverhaul.carvertypes;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import wftech.caveoverhaul.AirOnlyAquifer;

public class VanillaCanyon extends CanyonWorldCarver {

	public VanillaCanyon(Codec<CanyonCarverConfiguration> p_64711_) {
		super(p_64711_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isStartChunk(CanyonCarverConfiguration cfg, RandomSource random) {
		float flt = random.nextFloat();
		return flt <= cfg.probability;
	}


	@Override
	public boolean carve(CarvingContext p_224813_, 
			CanyonCarverConfiguration p_224814_, 
			ChunkAccess chunkAccess, 
			Function<BlockPos, Holder<Biome>> p_224816_, 
			RandomSource p_224817_, 
			Aquifer aquifer, 
			ChunkPos p_224819_, 
			CarvingMask p_224820_) {
		
		return super.carve(p_224813_, p_224814_, chunkAccess, p_224816_, p_224817_, new AirOnlyAquifer(chunkAccess, p_224817_.nextFloat() <=  0.3f), p_224819_, p_224820_);
	}
}
