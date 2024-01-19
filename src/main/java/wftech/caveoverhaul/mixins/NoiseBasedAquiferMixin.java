package wftech.caveoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.levelgen.Aquifer.NoiseBasedAquifer;
import wftech.caveoverhaul.CaveOverhaul;

@Mixin(NoiseBasedAquifer.class)
public class NoiseBasedAquiferMixin {
	
	@Inject(method="shouldScheduleFluidUpdate()Z", at=@At("HEAD"), remap=true, cancellable=true)
	private void scheduleFluidUpdateMixin(CallbackInfoReturnable<Boolean> cir) {
		if(CaveOverhaul.ENABLE_MULTILAYER_RIVERS) {
			cir.setReturnValue(true);
			return;
		}
	}
}
