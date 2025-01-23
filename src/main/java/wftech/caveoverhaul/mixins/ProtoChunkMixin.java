package wftech.caveoverhaul.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.utils.Globals;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

@Mixin(ProtoChunk.class)
public class ProtoChunkMixin {

    private static final ThreadLocal<BlockPos> currentBlockPos = new ThreadLocal<>();

    //SRG name: m_6978_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;
    /*
    @Inject(method="setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;", at=@At("HEAD"), cancellable=true, remap=true)
    private void getInterpolatedStateMixin(BlockPos bPos, BlockState bState, boolean unkbool, CallbackInfoReturnable<BlockState> cir) {

        Globals.init();
        if(!Globals.isVolcanicCavernsLoaded) {
            return;
        }
        // /tp 59 -21 4 deleted half my stuff

        if (NoiseChunkMixinUtils.shouldSetToStone(99999, bPos.getX(), bPos.getY(), bPos.getZ())) {
            BlockState preferredState = null;
            Block origBlock = bState.getBlock();
            if(bState.isAir()) {
                preferredState = bPos.getY() >= 0 ? Blocks.STONE.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState();
            } else {
                preferredState = bState;
            }

            if(preferredState.getBlock() == Blocks.STONE) {
                preferredState = Blocks.RED_STAINED_GLASS.defaultBlockState();
            }

            if(bPos.getZ() >= -10 && bPos.getZ() <= -1 && bPos.getX() >= 64 && bPos.getX() <= 85 && bPos.getY() <= -32) {
                CaveOverhaul.LOGGER.error("Setting river edge to " + preferredState + " = " + bState);
            }

            cir.setReturnValue(preferredState);
            cir.cancel();
        }
    }

     */

    //Keeping this around for a patch incase I need it
    /*
    @Inject(
            method = "setBlockState",
            at = @At("HEAD"),
            remap = true
    )
    private void captureBlockPos(BlockPos pos, BlockState state, boolean flag, CallbackInfoReturnable<BlockState> cir) {
        currentBlockPos.set(pos);
    }
     */

    @ModifyVariable(method="setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
            at=@At("HEAD"), remap=true, ordinal = 0)
    private BlockState getInterpolatedStateMixin(BlockState bState, BlockPos bPos) {

        Globals.init();
        if(!Globals.isVolcanicCavernsLoaded) {
            return bState;
        }

        //BlockPos bPos = currentBlockPos.get();
        // /tp 59 -21 4 deleted half my stuff

        if (NoiseChunkMixinUtils.shouldSetToStone(99999, bPos.getX(), bPos.getY(), bPos.getZ())) {
            BlockState preferredState = null;
            Block origBlock = bState.getBlock();
            if(bState.isAir()) {
                preferredState = bPos.getY() >= 0 ? Blocks.STONE.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState();
            } else {
                preferredState = bState;
            }

            return preferredState;
        }

        return bState;
    }

}