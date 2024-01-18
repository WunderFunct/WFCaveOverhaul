package wftech.caveoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseChunk.BlockStateFiller;

@Mixin(NoiseRouterData.class)
public interface NoiseRouterDataAccess {

	/*
	   private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
	   private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
	   private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
	   private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = createKey("overworld_amplified/factor");
	   private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
	   private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = createKey("overworld_amplified/depth");
	   private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
   private static final ResourceKey<DensityFunction> Y = createKey("y");
	   //private static DensityFunction noiseGradientDensity(DensityFunction p_212272_, DensityFunction p_212273_) {}
	   private static DensityFunction yLimitedInterpolatable(DensityFunction p_209472_, DensityFunction p_209473_, int p_209474_, int p_209475_, int p_209476_) {}
	   private static DensityFunction getFunction(HolderGetter<DensityFunction> p_256312_, ResourceKey<DensityFunction> p_256077_) {}
	   private static DensityFunction slideOverworld(boolean p_224490_, DensityFunction p_224491_) {}
	   private static DensityFunction postProcess(DensityFunction p_224493_) {}
	*/
	

	@Accessor("SHIFT_X")
	public static ResourceKey<DensityFunction> SHIFT_X() {
		throw new AssertionError();
	}
	
	@Accessor("SHIFT_Z")
	public static ResourceKey<DensityFunction> SHIFT_Z() {
		throw new AssertionError();
	}
	
	@Accessor("FACTOR_LARGE")
	public static ResourceKey<DensityFunction> FACTOR_LARGE() {
		throw new AssertionError();
	}
	
	//@Accessor("FACTOR_AMPLIFIED")
	//public static ResourceKey<DensityFunction> FACTOR_AMPLIFIED() {
	//	throw new AssertionError();
	//}
	
	@Accessor("DEPTH_LARGE")
	public static ResourceKey<DensityFunction> DEPTH_LARGE() {
		throw new AssertionError();
	}
	
	//@Accessor("DEPTH_AMPLIFIED")
	//public static ResourceKey<DensityFunction> DEPTH_AMPLIFIED() {
	//	throw new AssertionError();
	//}
	
	@Accessor("SLOPED_CHEESE")
	public static ResourceKey<DensityFunction> SLOPED_CHEESE() {
		throw new AssertionError();
	}
	
	@Accessor("Y")
	public static ResourceKey<DensityFunction> Y() {
		throw new AssertionError();
	}

	
	
	@Invoker("noiseGradientDensity")
	public static DensityFunction noiseGradientDensity(DensityFunction p_212272_, DensityFunction p_212273_) {
		throw new AssertionError();
	}

	@Invoker("yLimitedInterpolatable")
	public static DensityFunction yLimitedInterpolatable(DensityFunction p_209472_, DensityFunction p_209473_, int p_209474_, int p_209475_, int p_209476_) {
		throw new AssertionError();
	}

	@Invoker("getFunction")
	public static DensityFunction getFunction(ResourceKey<DensityFunction> p_256077_) {
		throw new AssertionError();
	}

	//@Invoker("slideOverworld")
	//public static DensityFunction slideOverworld(boolean p_224490_, DensityFunction p_224491_) {
	//	throw new AssertionError();
	//}

	//@Invoker("postProcess")
	//public static DensityFunction postProcess(DensityFunction p_224493_) {
	//	throw new AssertionError();
	//}
}
