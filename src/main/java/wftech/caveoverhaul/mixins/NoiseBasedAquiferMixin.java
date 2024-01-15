package wftech.caveoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Aquifer.NoiseBasedAquifer;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

@Mixin(NoiseBasedAquifer.class)
public class NoiseBasedAquiferMixin {
	
	@Inject(method="shouldScheduleFluidUpdate()Z", at=@At("HEAD"), remap=true, cancellable=true)
	private void scheduleFluidUpdateMixin(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
		return;
	}
	
	/*
	@Inject(method="computeSubstance(Lnet/minecraft/world/level/levelgen/DensityFunction$FunctionContext;D)Lnet/minecraft/world/level/block/state/BlockState;", at=@At("HEAD"), remap=true, cancellable=true)
	private void computeSubstanceMixin(DensityFunction.FunctionContext ctx, double p_208187_, CallbackInfoReturnable<BlockState> cir) {
		
		if(NoiseChunkMixinUtils.shouldSetToLava(128, ctx.blockX(), ctx.blockY(), ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToWater(128, ctx.blockX(), ctx.blockY(), ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToStone(128, ctx.blockX(), ctx.blockY(), ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToLava(128, ctx.blockX(), ctx.blockY() + 1, ctx.blockZ())) {
			return state;
		} else if(NoiseChunkMixinUtils.shouldSetToWater(128, ctx.blockX(), ctx.blockY() + 1, ctx.blockZ())) {
			return state;
		}
		
		return;
	}
	*/
}
