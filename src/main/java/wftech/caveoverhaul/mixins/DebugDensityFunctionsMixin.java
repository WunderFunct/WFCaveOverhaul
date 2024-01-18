package wftech.caveoverhaul.mixins;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import wftech.caveoverhaul.CaveOverhaul;

@Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions")
public class DebugDensityFunctionsMixin {


	@Inject(method="min(Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;)Lnet/minecraft/world/level/levelgen/DensityFunction;", 
			at=@At("HEAD"))
	private static void createInject(DensityFunction df1, DensityFunction df2,
			CallbackInfoReturnable ci) {

		//CaveOverhaul.LOGGER.error("[CO] DebugMixinDF 1 ============ ");
		//CaveOverhaul.LOGGER.error("[CO] DebugMixinDF 1 df1 -> " + df1);
		//CaveOverhaul.LOGGER.error("[CO] DebugMixinDF 1 df1 name -> " + df1.getClass().getName());
		//CaveOverhaul.LOGGER.error("[CO] DebugMixinDF 1 dfs -> " + df2);
		//CaveOverhaul.LOGGER.error("[CO] DebugMixinDF 1 dfs name -> " + df2.getClass().getName());
		for(StackTraceElement el: Thread.currentThread().getStackTrace()) {
			//CaveOverhaul.LOGGER.error("[CO] DebugMixinDF 1 -> " + el);
		}
	}
}
