package wftech.caveoverhaul.carvertypes;

import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import wftech.caveoverhaul.AirOnlyAquifer;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;

public class OldWorldCarverv12ReverseNoiseDistribution extends OldWorldCarverv12 {

	public OldWorldCarverv12ReverseNoiseDistribution(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
	@Override
    protected boolean shouldCarve(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, RandomSource random, ChunkPos chunkPos_) {

		float odds2 = Config.settings.get(Config.KEY_CAVE_CHANCE);

		return random.nextFloat() <= odds2;
    }
	
	@Override
	public boolean isStartChunk(CaveCarverConfiguration p_224894_, RandomSource p_224895_) {
		return true;
	}
	
}
