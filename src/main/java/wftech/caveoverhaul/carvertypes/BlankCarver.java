package wftech.caveoverhaul.carvertypes;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import wftech.caveoverhaul.AirOnlyAquifer;

public class BlankCarver extends CanyonWorldCarver {

	public static final Codec<CanyonCarverConfiguration> CODEC = RecordCodecBuilder.create(
			p_158984_ -> p_158984_.group(
							CarverConfiguration.CODEC.forGetter(p_158990_ -> p_158990_),
							FloatProvider.CODEC.fieldOf("vertical_rotation").forGetter(p_158988_ -> p_158988_.verticalRotation),
							CanyonCarverConfiguration.CanyonShapeConfiguration.CODEC.fieldOf("shape").forGetter(p_158986_ -> p_158986_.shape)
					)
					.apply(p_158984_, CanyonCarverConfiguration::new)
	);

	public BlankCarver(Codec<CanyonCarverConfiguration> p_64711_) {
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

		return true;
	}
}
