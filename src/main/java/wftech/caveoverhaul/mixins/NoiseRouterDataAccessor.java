package wftech.caveoverhaul.mixins;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NoiseRouterData.class)
public interface NoiseRouterDataAccessor {

    @Invoker("getFunction")
    public static DensityFunction getFunction(HolderGetter<DensityFunction> holderGetter, ResourceKey<DensityFunction> resourceKey) {
        throw new AssertionError();
    };

    @Invoker("noiseGradientDensity")
    public static DensityFunction noiseGradientDensity(DensityFunction densityFunction, DensityFunction densityFunction2) {
        throw new AssertionError();
    };

    @Invoker("postProcess")
    public static DensityFunction postProcess(DensityFunction function) {
        throw new AssertionError();
    }

    @Invoker("yLimitedInterpolatable")
    public static DensityFunction yLimitedInterpolatable(DensityFunction densityFunction, DensityFunction densityFunction2, int i, int j, int k) {
        throw new AssertionError();
    }

    @Invoker("slideOverworld")
    public static DensityFunction slideOverworld(boolean bl, DensityFunction densityFunction) {
        throw new AssertionError();
    }




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

    @Accessor("FACTOR_AMPLIFIED")
    public static ResourceKey<DensityFunction> FACTOR_AMPLIFIED() {
        throw new AssertionError();
    }

    @Accessor("FACTOR")
    public static ResourceKey<DensityFunction> FACTOR() {
        throw new AssertionError();
    }

    @Accessor("DEPTH_LARGE")
    public static ResourceKey<DensityFunction> DEPTH_LARGE() {
        throw new AssertionError();
    }

    @Accessor("DEPTH_AMPLIFIED")
    public static ResourceKey<DensityFunction> DEPTH_AMPLIFIED() {
        throw new AssertionError();
    }

    @Accessor("DEPTH")
    public static ResourceKey<DensityFunction> DEPTH() {
        throw new AssertionError();
    }

    @Accessor("SLOPED_CHEESE")
    public static ResourceKey<DensityFunction> SLOPED_CHEESE() {
        throw new AssertionError();
    }

    @Accessor("Y")
    public static ResourceKey<DensityFunction> Y() {
        throw new AssertionError();
    }

    @Accessor("CONTINENTS_LARGE")
    public static ResourceKey<DensityFunction> CONTINENTS_LARGE() {
        throw new AssertionError();
    }

    @Accessor("CONTINENTS")
    public static ResourceKey<DensityFunction> CONTINENTS() {
        throw new AssertionError();
    }

    @Accessor("EROSION_LARGE")
    public static ResourceKey<DensityFunction> EROSION_LARGE() {
        throw new AssertionError();
    }

    @Accessor("EROSION")
    public static ResourceKey<DensityFunction> EROSION() {
        throw new AssertionError();
    }

    @Accessor("RIDGES")
    public static ResourceKey<DensityFunction> RIDGES() {
        throw new AssertionError();
    }

    @Accessor("NOODLE")
    public static ResourceKey<DensityFunction> NOODLE() {
        throw new AssertionError();
    }

    @Accessor("SPAGHETTI_2D_THICKNESS_MODULATOR")
    public static ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR() {
        throw new AssertionError();
    }

    @Accessor("SPAGHETTI_2D")
    public static ResourceKey<DensityFunction> SPAGHETTI_2D() {
        throw new AssertionError();
    }

    @Accessor("ZERO")
    public static ResourceKey<DensityFunction> ZERO() {
        throw new AssertionError();
    }
}