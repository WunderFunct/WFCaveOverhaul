package wftech.caveoverhaul;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Aquifer.NoiseBasedAquifer;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.SinglePointContext;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

//public class AirOnlyAquifer implements Aquifer {
public class AirOnlyAquifer implements Aquifer {

	protected ChunkAccess level = null;
	protected boolean exposeToAir = false;
	protected int x = 0;
	protected int y = 0;
	protected int z = 0;
	
	public AirOnlyAquifer(ChunkAccess level, boolean exposeToAir) {
		this.level = level;
		this.exposeToAir = exposeToAir;
	}
	
	public AirOnlyAquifer(ChunkAccess chunkPrimer, boolean b, int blockX, int yIter, int blockZ) {
		this.level = chunkPrimer;
		this.exposeToAir = b;
		this.x = blockX;
		this.y = yIter;
		this.z = blockZ;
	}

	public boolean isLiquid(BlockState state) {
		return state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER || state.liquid();
	}

	@Override
	public BlockState computeSubstance(FunctionContext ctx, double p_208159_) {
		
		if(this.level == null) {
			return Blocks.AIR.defaultBlockState();
		}
		
		BlockState state = this.level.getBlockState(new BlockPos(ctx.blockX(), ctx.blockY(), ctx.blockZ()));
		
		///tp -656 60 138
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

		BlockPos basePos = new BlockPos(ctx.blockX(), ctx.blockY(), ctx.blockZ());
		BlockPos pos_n = new BlockPos(ctx.blockX(), ctx.blockY(), ctx.blockZ() - 1);
		BlockPos pos_s = new BlockPos(ctx.blockX(), ctx.blockY(), ctx.blockZ() + 1);
		BlockPos pos_e = new BlockPos(ctx.blockX() + 1, ctx.blockY(), ctx.blockZ());
		BlockPos pos_w = new BlockPos(ctx.blockX() - 1, ctx.blockY(), ctx.blockZ());
		BlockPos pos_u = new BlockPos(ctx.blockX(), ctx.blockY() + 1, ctx.blockZ());
		BlockPos pos_u2 = new BlockPos(ctx.blockX(), ctx.blockY() + 2, ctx.blockZ());
		BlockPos pos_u3 = new BlockPos(ctx.blockX(), ctx.blockY() + 3, ctx.blockZ());
		
		ChunkPos cbasePos = new ChunkPos(basePos);
		ChunkPos cpos_n = new ChunkPos(pos_n);
		ChunkPos cpos_s = new ChunkPos(pos_s);
		ChunkPos cpos_e = new ChunkPos(pos_e);
		ChunkPos cpos_w = new ChunkPos(pos_w);

		BlockState state_n = this.level.getBlockState(pos_n);
		BlockState state_s = this.level.getBlockState(pos_s);
		BlockState state_e = this.level.getBlockState(pos_e);
		BlockState state_w = this.level.getBlockState(pos_w);
		BlockState state_u = this.level.getBlockState(pos_u);
		BlockState state_u2 = this.level.getBlockState(pos_u2);
		BlockState state_u3 = this.level.getBlockState(pos_u3);
		
		if(cbasePos.x == cpos_w.x && cbasePos.z == cpos_w.z && this.isLiquid(state_w)) {
			return state;
		}
		if(cbasePos.x == cpos_e.x && cbasePos.z == cpos_e.z && this.isLiquid(state_e)) {
			return state;
		}
		if(cbasePos.x == cpos_n.x && cbasePos.z == cpos_n.z && this.isLiquid(state_n)) {
			return state;
		}
		
		if(cbasePos.x == cpos_s.x && cbasePos.z == cpos_s.z && this.isLiquid(state_s)) {
			return state;
		}
		
		///tp -1042 63.37 -54.94
		if(this.isLiquid(state_u) || this.isLiquid(state_u2) || this.isLiquid(state_u3)) {
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

		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean shouldScheduleFluidUpdate() {
		return false;
	}

}
