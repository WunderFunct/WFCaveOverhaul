package wftech.caveoverhaul.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.WorldGenUtils;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernMiddleLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernMiddleLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer3;
import wftech.caveoverhaul.carvertypes.rivers.*;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin {

	@Inject(method="getInterpolatedState()Lnet/minecraft/world/level/block/state/BlockState;", at=@At("HEAD"), cancellable=true, remap=true)
	private void getInterpolatedStateMixin(CallbackInfoReturnable<BlockState> cir) {
		NoiseChunk thisChunk = (NoiseChunk) (Object) this;
		
		boolean isLikelyOverworld = WorldGenUtils.checkIfLikelyOverworld(((NoiseChunkAccessor) this).getNoiseSettings());
		if(!isLikelyOverworld) {
			return;
		}
		
		int x = thisChunk.blockX();
		int y = thisChunk.blockY();
		int z = thisChunk.blockZ();
		
		int topY = thisChunk.preliminarySurfaceLevel(x, z);
		topY = topY - 8;
		
		if(y >= topY) {
			return;
		}
		
		/*
		 * RED_STAINED_GLASS = lava
		 * GRAY_STAINED_GLASS = water
		 * YELLOW_STAINED_GLASS = stone
		 * BLACK_STAINED_GLASS = air above rivers
		 */
		
		if(NoiseChunkMixinUtils.shouldSetToLava(topY, x, y, z)) {
			cir.setReturnValue(Blocks.LAVA.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToWater(topY, x, y, z)) {
			cir.setReturnValue(Blocks.WATER.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToStone(topY, x, y, z)) {
			cir.setReturnValue(Blocks.STONE.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x, y, z)) {
			cir.setReturnValue(Blocks.AIR.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x, y, z)) {
			cir.setReturnValue(Blocks.AIR.defaultBlockState());
			cir.cancel();
			return;
		}
	}
}
