package wftech.caveoverhaul.mixins;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import wftech.caveoverhaul.CaveOverhaul;

@Mixin(NoiseRouterWithOnlyNoises.class)
public class DebugNoiseRouterWithOnlyNoisesMixin {

	@Inject(method="field(Ljava/lang/String;Ljava/util/function/Function;)Lcom/mojang/serialization/codecs/RecordCodecBuilder;", 
			at=@At("HEAD"), 
			remap=true, 
			cancellable=false)
	private static void fieldInject(
			String fieldName, 
			Function<NoiseRouterWithOnlyNoises, DensityFunction> someFunc,
			CallbackInfoReturnable<RecordCodecBuilder> cir) {

		//CaveOverhaul.LOGGER.error("[CO] DebugMixin 1 ============ ");
		//CaveOverhaul.LOGGER.error("[CO] DebugMixin 1 fieldName -> " + fieldName);
		//CaveOverhaul.LOGGER.error("[CO] DebugMixin 1 someFunc -> " + someFunc);
		for(StackTraceElement el: Thread.currentThread().getStackTrace()) {
			//CaveOverhaul.LOGGER.error("[CO] DebugMixin 1 -> " + el);
		}
	}

	@Inject(method="mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouterWithOnlyNoises;", 
			at=@At("HEAD"), 
			remap=true, 
			cancellable=false)
	private void mapAllInject(
			DensityFunction.Visitor visitor,
			CallbackInfoReturnable<NoiseRouterWithOnlyNoises> cir) {

		//CaveOverhaul.LOGGER.error("[CO] DebugMixin 2 ============ ");
		//CaveOverhaul.LOGGER.error("[CO] DebugMixin 2 Visitor -> " + visitor);
		//CaveOverhaul.LOGGER.error("[CO] DebugMixin 2 Visitor -> " + visitor.getClass().getName());
		for(StackTraceElement el: Thread.currentThread().getStackTrace()) {
			//CaveOverhaul.LOGGER.error("[CO] DebugMixin 2 -> " + el);
		}
		
	}
}
