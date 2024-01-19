package wftech.caveoverhaul;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Aquifer.NoiseBasedAquifer;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;

//public class AirOnlyAquifer implements Aquifer {
public class NoiseBasedSurfacePunctureAirOnlyAquifer implements Aquifer {

	public static FastNoiseLite noise = null;
	
	protected ChunkAccess level = null;
	protected boolean exposeToAir = false;
	
	public NoiseBasedSurfacePunctureAirOnlyAquifer(ChunkAccess level, boolean exposeToAir) {
		this.level = level;
		this.exposeToAir = exposeToAir;
	}
	
	public static void initNoise() {
		if(noise != null) {
			return;
		}

		FastNoiseLite tnoise = new FastNoiseLite();
		tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed());
		tnoise.SetNoiseType(NoiseType.ValueCubic);
		tnoise.SetFrequency(0.015f);
		
		noise = tnoise;
	}
	
	public static float getNoise2D(int x, int z) {
		if(noise == null) {
			initNoise();
		}
		
		return noise.GetNoise(x, z);
	}
	
	public boolean isLiquid(BlockState state) {
		return state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER;
	}

	@Override
	public BlockState computeSubstance(FunctionContext ctx, double p_208159_) {
		
		if(this.level == null) {
			//CHANGED, was Blocks.CAVE_AIR.defaultBlockState()
			return Blocks.AIR.defaultBlockState();
		}
		
		BlockState state = this.level.getBlockState(new BlockPos(ctx.blockX(), ctx.blockY(), ctx.blockZ()));
		if(state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER) {
			return state;
		}
		
		float fNoise = getNoise2D(ctx.blockX(), ctx.blockZ());
		float fNoiseCutoff = 0f;
		
		int topHeight = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX(), ctx.blockZ());
		if(ctx.blockY() >= (topHeight - 1) && !this.exposeToAir) {
			if(fNoise > fNoiseCutoff) {
				return Blocks.AIR.defaultBlockState();
			}
			return state;
		}
		
		int topHeight_1 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX() + 1, ctx.blockZ());
		if(ctx.blockY() >= (topHeight_1 - 2) && !this.exposeToAir) {
			if(fNoise > fNoiseCutoff) {
				return Blocks.AIR.defaultBlockState();
			}
			return state;
		}
		
		int topHeight_2 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX() - 1, ctx.blockZ());
		if(ctx.blockY() >= (topHeight_2 - 2) && !this.exposeToAir) {
			if(fNoise > fNoiseCutoff) {
				return Blocks.AIR.defaultBlockState();
			}
			return state;
		}
		
		int topHeight_3 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX(), ctx.blockZ() + 1);
		if(ctx.blockY() >= (topHeight_3 - 2) && !this.exposeToAir) {
			if(fNoise > fNoiseCutoff) {
				return Blocks.AIR.defaultBlockState();
			}
			return state;
		}
		
		int topHeight_4 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX(), ctx.blockZ() - 1);
		if(ctx.blockY() >= (topHeight_4 - 2) && !this.exposeToAir) {
			if(fNoise > fNoiseCutoff) {
				return Blocks.AIR.defaultBlockState();
			}
			return state;
		}

		BlockState state_n = this.level.getBlockState(new BlockPos((ctx.blockX() % 16) + 1 > 15 ? ctx.blockX() : ctx.blockX() + 1, ctx.blockY(), ctx.blockZ()));
		BlockState state_e = this.level.getBlockState(new BlockPos((ctx.blockX() % 16) - 1 < 0 ? ctx.blockX() : ctx.blockX() - 1, ctx.blockY(), ctx.blockZ()));
		BlockState state_w = this.level.getBlockState(new BlockPos(ctx.blockX(), ctx.blockY(), (ctx.blockZ() % 16) + 1 > 15 ? ctx.blockZ() : ctx.blockZ() + 1));
		BlockState state_s = this.level.getBlockState(new BlockPos(ctx.blockX(), ctx.blockY(), (ctx.blockZ() % 16) - 1 < 0 ? ctx.blockZ() : ctx.blockZ() - 1));
		BlockState state_u = this.level.getBlockState(new BlockPos(ctx.blockX(), ctx.blockY() + 1, ctx.blockZ()));
		if(this.isLiquid(state_n) || this.isLiquid(state_s) || this.isLiquid(state_e) || this.isLiquid(state_w) || this.isLiquid(state_u)) {
			return state;
		}

		//CHANGED, was Blocks.CAVE_AIR.defaultBlockState()
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean shouldScheduleFluidUpdate() {
		return false;
	}

}
