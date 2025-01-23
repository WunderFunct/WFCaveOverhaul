package wftech.caveoverhaul.mixins;

import net.minecraft.world.level.chunk.CarvingMask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.utils.Globals;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

@Mixin(CarvingMask.class)
public class CarvingMaskMixin {

    //YOU CAN'T STOP ME
    //I hope this doesn't lag everyone out lol
    //sorry in advanced I guess
    //especially if this is way in the future and someone is working on this code who isn't the original maker
    @Inject(method="set(III)V", at=@At("HEAD"), cancellable=true, remap=true)
    private void getInterpolatedStateMixin(int x, int y, int z, CallbackInfo ci) {

        Globals.init();
        if(!Globals.isVolcanicCavernsLoaded) {
            return;
        }

        if (NoiseChunkMixinUtils.shouldSetToStone(99999, x, y, z)) {
            ci.cancel();
        }
    }
}