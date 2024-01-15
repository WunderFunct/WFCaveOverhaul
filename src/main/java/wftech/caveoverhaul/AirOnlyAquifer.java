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
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

//public class AirOnlyAquifer implements Aquifer {
public class AirOnlyAquifer implements Aquifer {

	protected ChunkAccess level = null;
	protected boolean exposeToAir = false;
	
	public AirOnlyAquifer(ChunkAccess level, boolean exposeToAir) {
		this.level = level;
		this.exposeToAir = exposeToAir;
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
		
		int topHeight = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX(), ctx.blockZ());
		if(ctx.blockY() >= (topHeight - 1) && !this.exposeToAir) {
			return state;
		}
		
		int topHeight_1 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX() + 1, ctx.blockZ());
		if(ctx.blockY() >= (topHeight_1 - 2) && !this.exposeToAir) {
			return state;
		}
		
		int topHeight_2 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX() - 1, ctx.blockZ());
		if(ctx.blockY() >= (topHeight_2 - 2) && !this.exposeToAir) {
			return state;
		}
		
		int topHeight_3 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX(), ctx.blockZ() + 1);
		if(ctx.blockY() >= (topHeight_3 - 2) && !this.exposeToAir) {
			return state;
		}
		
		int topHeight_4 = this.level.getHeight(Types.WORLD_SURFACE_WG, ctx.blockX(), ctx.blockZ() - 1);
		if(ctx.blockY() >= (topHeight_4 - 2) && !this.exposeToAir) {
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
		
		if(NoiseChunkMixinUtils.shouldSetToLava(topHeight, ctx.blockX(), ctx.blockY(), ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToWater(topHeight, ctx.blockX(), ctx.blockY(), ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToStone(topHeight, ctx.blockX(), ctx.blockY(), ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToLava(topHeight, ctx.blockX(), ctx.blockY() + 1, ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToWater(topHeight, ctx.blockX(), ctx.blockY() + 1, ctx.blockZ())) {
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