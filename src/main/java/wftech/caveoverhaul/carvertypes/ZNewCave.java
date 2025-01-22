package wftech.caveoverhaul.carvertypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import wftech.caveoverhaul.AirOnlyAquifer;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ZNewCave extends CaveWorldCarver {

	/*
	 * Can't do literal 1.16.5- caves due to the new heights
	 * With the introduction of deepslate, there's a great chance to rebalance cave densities around
	 * the deepslate introduction layer. It'll create a sense of how deep the player is :)
	 */
	public static final Codec<CaveCarverConfiguration> CODEC = RecordCodecBuilder.create(
			p_159184_ -> p_159184_.group(
							CarverConfiguration.CODEC.forGetter(p_159192_ -> p_159192_),
							FloatProvider.CODEC.fieldOf("horizontal_radius_multiplier").forGetter(p_159190_ -> p_159190_.horizontalRadiusMultiplier),
							FloatProvider.CODEC.fieldOf("vertical_radius_multiplier").forGetter(p_159188_ -> p_159188_.verticalRadiusMultiplier),
							FloatProvider.codec(-1.0F, 1.0F).fieldOf("floor_level").forGetter(p_159186_ -> p_159186_.floorLevel)
					)
					.apply(p_159184_, CaveCarverConfiguration::new)
	);

	public ZNewCave(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
    public int getCaveY(RandomSource p_230361_1_, boolean shallow) {
    	if(shallow) {
    		return 130 - p_230361_1_.nextInt(p_230361_1_.nextInt(120) + 1); //130 = average y I'd like the caves to start at
    	} else {
	    	return p_230361_1_.nextInt(p_230361_1_.nextInt(384) + 8);
    	}
    }

    
    @Override
    protected float getThickness(RandomSource p_230359_1_) {
        float lvt_2_1_ = p_230359_1_.nextFloat() * 2.0f + p_230359_1_.nextFloat();
        if (p_230359_1_.nextInt(10) == 0) {
            lvt_2_1_ *= p_230359_1_.nextFloat() * p_230359_1_.nextFloat() * 3.0f + 1.0f;
        }
        return lvt_2_1_;
    }

    protected boolean shouldCarve(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, RandomSource random, ChunkPos chunkPos) {
    	return true;
    }

	@Override
	public boolean carve(
		   CarvingContext ctx, 
		   CaveCarverConfiguration cfg, 
		   ChunkAccess level, 
		   Function<BlockPos, Holder<Biome>> pos2BiomeMapping, 
		   RandomSource random, 
		   Aquifer disabled, 
		   ChunkPos chunkPos, 
		   CarvingMask mask) {

		int x_o = chunkPos.getBlockX(0);
		int y_o = 0;
		int z_o = chunkPos.getBlockZ(0);

		MutableBlockPos mutableBlockPos = new MutableBlockPos(x_o, y_o, z_o);
		MutableBlockPos mutableBlockPos1 = new MutableBlockPos(x_o, y_o, z_o);
		MutableBoolean shouldCarve = new MutableBoolean(false);

		this.carveBlock(ctx, cfg, level, pos2BiomeMapping, mask, mutableBlockPos,
				mutableBlockPos1, disabled, shouldCarve);
		
		return true;
	}
	
	@Override
	public int getRange() {
		return 8;
	}



}