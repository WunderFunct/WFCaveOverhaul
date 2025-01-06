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
import wftech.caveoverhaul.Config;

public class VanillaCanyon extends CanyonWorldCarver {

	public VanillaCanyon(Codec<CanyonCarverConfiguration> p_64711_) {
		super(p_64711_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isStartChunk(CanyonCarverConfiguration cfg, RandomSource random) {
		float flt = random.nextFloat();
		if (cfg.yScale.getMinValue() > 3) {
			return flt <= Config.settings.get(Config.KEY_CANYON_LOWER_CHANCE);
		} else {
			return flt <= Config.settings.get(Config.KEY_CANYON_UPPER_CHANCE);
		}
		//return flt <= cfg.probability;
	}


	@Override
	public boolean carve(CarvingContext p_224813_, 
			CanyonCarverConfiguration cfg,
			ChunkAccess chunkAccess, 
			Function<BlockPos, Holder<Biome>> p_224816_, 
			RandomSource rndm,
			Aquifer aquifer, 
			ChunkPos p_224819_, 
			CarvingMask p_224820_) {

		//return super.carve(p_224813_, p_224814_, chunkAccess, p_224816_, p_224817_, new AirOnlyAquifer(chunkAccess, p_224817_.nextFloat() <=  Config.settings.get(Config.KEY_CANYON_UPPER_AIR_EXPOSURE)), p_224819_, p_224820_);

		//copy and paste from CanyonWorldCarver with y values being messed

		Aquifer airAquifer = new AirOnlyAquifer(chunkAccess, rndm.nextFloat() <=  Config.settings.get(Config.KEY_CANYON_UPPER_AIR_EXPOSURE));


		int i = (this.getRange() * 2 - 1) * 16;
		double d0 = (double)p_224819_.getBlockX(rndm.nextInt(16));

		//changed this
		//int j = p_224814_.y.sample(rndm, p_224813_);
		//low y is -16 yo -64, with high y being 180 to -64
		int coord_y = 0;
		if (cfg.yScale.getMinValue() > 3) {
			//lower
			int scaledLower = (int) (chunkAccess.getMinBuildHeight() * 0.25);
			coord_y = rndm.nextInt(chunkAccess.getMinBuildHeight(), scaledLower);
		} else {
			//upper
			coord_y = rndm.nextInt(chunkAccess.getMinBuildHeight(), 180);
		}

		double d1 = (double)p_224819_.getBlockZ(rndm.nextInt(16));
		float f = rndm.nextFloat() * (float) (Math.PI * 2);
		float f1 = cfg.verticalRotation.sample(rndm);
		double d2 = (double)cfg.yScale.sample(rndm);
		float f2 = cfg.shape.thickness.sample(rndm);
		int k = (int)((float)i * cfg.shape.distanceFactor.sample(rndm));
		int l = 0;
		super.doCarve(p_224813_, cfg, chunkAccess, p_224816_, rndm.nextLong(), airAquifer, d0, (double)coord_y, d1, f2, f, f1, 0, k, d2, p_224820_);
		return true;

	}
}
