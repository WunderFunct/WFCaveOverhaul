package wftech.caveoverhaul.mixins;

import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wftech.caveoverhaul.utils.FabricUtils;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {

    //Thread.currentThread().getThreadGroup();
    @Inject(method="initServer()Z", at=@At("HEAD"), remap=true)
    private void initServerMixin(CallbackInfoReturnable<Boolean> cir) {
        FabricUtils.server = (DedicatedServer) (Object) this;
    }
}
