package wftech.caveoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseChunk.BlockStateFiller;
import net.minecraft.world.level.levelgen.NoiseSettings;

@Mixin(NoiseChunk.class)
public interface NoiseChunkAccessor {

	@Accessor("noiseSettings")
	public NoiseSettings getNoiseSettings();

	@Accessor("blockStateRule")
	public BlockStateFiller getBlockStateRule();

	@Invoker("computePreliminarySurfaceLevel")
	public int invokeGetSurfaceLevel(long longpos);
}
