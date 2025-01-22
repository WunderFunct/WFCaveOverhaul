package wftech.caveoverhaul.mixins;

import com.mojang.logging.LogUtils;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DensityFunctions.Constant.class)
public class DFConstantMixin {

    @Inject(method = "maxValue()D",
            at=@At("RETURN"), cancellable=true, remap=true)
    private void overrideMax(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(9999.0);
        cir.cancel();
    }

    @Inject(method = "minValue()D",
            at=@At("RETURN"), cancellable=true, remap=true)
    private void overrideMin(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(-9999.0);
        cir.cancel();
    }
}