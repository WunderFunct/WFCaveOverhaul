package wftech.caveoverhaul.mixins;

import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wftech.caveoverhaul.utils.IMixinHelperNCFlatCache;

@Mixin(NoiseChunk.FlatCache.class)
public class NCFlatCacheMixin implements IMixinHelperNCFlatCache {

    private boolean unknownBool;

    //FlatCache(DensityFunction p_209330_, boolean p_209331_)
    @Inject(method="<init> (Lnet/minecraft/world/level/levelgen/NoiseChunk;Lnet/minecraft/world/level/levelgen/DensityFunction;Z)V",
            at=@At("RETURN"), cancellable=false, remap=true)
    private void constructorMixin(NoiseChunk p_209330_, DensityFunction p_209331_, boolean someBool, CallbackInfo ci){
        ((NCFlatCacheMixin) (Object) this).setUnknownBool(someBool);
    }

    @Override
    public void setUnknownBool(boolean unknownBool) {
        this.unknownBool = unknownBool;
    }

    @Override
    public boolean getUnknownBool() {
        return this.unknownBool;
    }
}