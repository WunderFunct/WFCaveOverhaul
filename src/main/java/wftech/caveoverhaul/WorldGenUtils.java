package wftech.caveoverhaul;

import java.util.Iterator;
import java.util.stream.Stream;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import wftech.caveoverhaul.mixins.NoiseRouterDataAccessor;
import wftech.caveoverhaul.mixins.VeinTypeAccessor;
import wftech.caveoverhaul.utils.FabricUtils;

public class WorldGenUtils {

    public static NoiseRouter overworld() {
        boolean bool1 = false;
        boolean bool2 = false;

        Registry<DensityFunction> hg_density_function = BuiltinRegistries.DENSITY_FUNCTION;
        Registry<NoiseParameters> hg_noise = BuiltinRegistries.NOISE;

        //Registry<DensityFunction> registry_df = registries.registryOrThrow(Registry.DENSITY_FUNCTION_REGISTRY);
        //Registry<NoiseParameters> hg_noise = registries.registryOrThrow(Registry.NOISE_REGISTRY);

        DensityFunction densityfunction = DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.AQUIFER_BARRIER), 0.5D);
        DensityFunction densityfunction1 = DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
        DensityFunction densityfunction2 = DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143D);
        DensityFunction densityfunction3 = DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.AQUIFER_LAVA));

        DensityFunction densityfunction4 = NoiseRouterDataAccessor.getFunction(BuiltinRegistries.DENSITY_FUNCTION, NoiseRouterDataAccessor.SHIFT_X());
        DensityFunction densityfunction5 = NoiseRouterDataAccessor.getFunction(hg_density_function, NoiseRouterDataAccessor.SHIFT_Z());
        DensityFunction densityfunction6 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, hg_noise.getHolderOrThrow(bool1 ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, hg_noise.getHolderOrThrow(bool1 ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        DensityFunction densityfunction8 = NoiseRouterDataAccessor.getFunction(hg_density_function, bool1 ? NoiseRouterDataAccessor.FACTOR_LARGE() : (bool2 ? NoiseRouterDataAccessor.FACTOR_AMPLIFIED() : NoiseRouterDataAccessor.FACTOR()));
        DensityFunction densityfunction9 = NoiseRouterDataAccessor.getFunction(hg_density_function, bool1 ? NoiseRouterDataAccessor.DEPTH_LARGE() : (bool2 ? NoiseRouterDataAccessor.DEPTH_AMPLIFIED() : NoiseRouterDataAccessor.DEPTH()));
        DensityFunction densityfunction10 = NoiseRouterDataAccessor.noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
        DensityFunction densityfunction14 = NoiseRouterDataAccessor.postProcess(NoiseRouterDataAccessor.getFunction(hg_density_function, NoiseRouterDataAccessor.SLOPED_CHEESE()));
        DensityFunction densityfunction15 = NoiseRouterDataAccessor.getFunction(hg_density_function, NoiseRouterDataAccessor.Y());

        int i = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_224495_) -> {
            return ((VeinTypeAccessor) (Object) p_224495_).minY();
        }).min().orElse(-DimensionType.MIN_Y * 2);
        int j = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_224457_) -> {
            return ((VeinTypeAccessor) (Object) p_224457_).maxY();
        }).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction densityfunction16 = NoiseRouterDataAccessor.yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.ORE_VEININESS), 1.5D, 1.5D), i, j, 0);
        float f = 4.0F;
        DensityFunction densityfunction17 = NoiseRouterDataAccessor.yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.ORE_VEIN_A), 4.0D, 4.0D), i, j, 0).abs();
        DensityFunction densityfunction18 = NoiseRouterDataAccessor.yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.ORE_VEIN_B), 4.0D, 4.0D), i, j, 0).abs();
        DensityFunction densityfunction19 = DensityFunctions.add(DensityFunctions.constant((double)-0.08F), DensityFunctions.max(densityfunction17, densityfunction18));
        DensityFunction densityfunction20 = DensityFunctions.noise(hg_noise.getHolderOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(
                densityfunction,
                densityfunction1,
                densityfunction2,
                densityfunction3,
                densityfunction6,
                densityfunction7,
                NoiseRouterDataAccessor.getFunction(hg_density_function, bool1 ? NoiseRouterDataAccessor.CONTINENTS_LARGE() : NoiseRouterDataAccessor.CONTINENTS()),
                NoiseRouterDataAccessor.getFunction(hg_density_function, bool1 ? NoiseRouterDataAccessor.EROSION_LARGE() : NoiseRouterDataAccessor.EROSION()),
                densityfunction9,
                NoiseRouterDataAccessor.getFunction(hg_density_function, NoiseRouterDataAccessor.RIDGES()),
                NoiseRouterDataAccessor.slideOverworld(
                        bool2,
                        DensityFunctions.add(
                                densityfunction10,
                                DensityFunctions.constant(-0.703125D)
                        ).clamp(-64.0D, 64.0D)),
                densityfunction14, //replace
                densityfunction16,
                densityfunction19,
                densityfunction20);
    }

    public static boolean checkIfSameNGS(NoiseGeneratorSettings settings1, NoiseGeneratorSettings settings2, long seed) {
        boolean b1 = settings1.aquifersEnabled() == settings2.aquifersEnabled();
        boolean b2 = settings1.defaultBlock() == settings2.defaultBlock();
        boolean b3 = settings1.defaultFluid() == settings2.defaultFluid();
        boolean b4 = settings1.disableMobGeneration() == settings2.disableMobGeneration();
        boolean b5 = settings1.isAquifersEnabled() == settings2.isAquifersEnabled();
        boolean b6 = settings1.oreVeinsEnabled() == settings2.oreVeinsEnabled();
        boolean b7 = settings1.seaLevel() == settings2.seaLevel();
        boolean b8 = settings1.spawnTarget() == settings2.spawnTarget();
        boolean b9 = settings1.surfaceRule() == settings2.surfaceRule();
        boolean b10 = settings1.getRandomSource() == settings2.getRandomSource();
        boolean b11 = settings1.noiseRouter() == settings2.noiseRouter();
        boolean b12 = settings1.noiseSettings() == settings2.noiseSettings();

        Iterator<ServerLevel> it = FabricUtils.server.getAllLevels().iterator();
        ServerLevel sLevel = null;
        int i = 0;
        while(it.hasNext()) {
            i += 1;
            sLevel = it.next();
        }

        return i == 0;

    }

    public static boolean checkIfLikelyOverworld(NoiseSettings settings) {
        boolean rightHeight = settings.height() == 384;
        boolean rightDepth = settings.minY() == -64;
        boolean rightRatioVertical = settings.noiseSizeHorizontal() == 1;
        //boolean rightRatioHorizontal = settings.noiseSizeVertical() == 2;

        return rightHeight && rightDepth && rightRatioVertical /*&& rightRatioHorizontal*/;
    }
}
