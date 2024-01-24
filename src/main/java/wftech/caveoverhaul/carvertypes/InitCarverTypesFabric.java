package wftech.caveoverhaul.carvertypes;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import wftech.caveoverhaul.CaveOverhaul;

public class InitCarverTypesFabric {

    public static final WorldCarver<CanyonCarverConfiguration> VANILLA_CANYON = new VanillaCanyon(CanyonCarverConfiguration.CODEC);
    public static final WorldCarver<CaveCarverConfiguration> CAVES_NOISE_DISTRIBUTION = new OldWorldCarverv12ReverseNoiseDistribution(CaveCarverConfiguration.CODEC);

    public static void init() {
        /*
        Register the caves/canyons
         */
        ResourceLocation vanilla_canyon_rloc = new ResourceLocation(CaveOverhaul.MOD_ID, "vanilla_canyon");
        ResourceLocation caves_noise_distribution_rloc = new ResourceLocation(CaveOverhaul.MOD_ID, "caves_noise_distribution");
        Registry.register(BuiltInRegistries.CARVER, vanilla_canyon_rloc, VANILLA_CANYON);
        Registry.register(BuiltInRegistries.CARVER, caves_noise_distribution_rloc, CAVES_NOISE_DISTRIBUTION);

        /*
        Minecraft will at some point take the above registered caves and canyons,
        then load a configured carver (defined in resources/data/caveoverhaul/worldgen/configured_carver),
        then reference the BiomeModifications's modifications to add the carvers based on the ResourceKey of our
        newly registered carvers
         */

        /*
        Declare keys
         */
        ResourceLocation canyons_rloc = new ResourceLocation(CaveOverhaul.MOD_ID, "canyons");
        ResourceLocation canyons_low_y_rloc = new ResourceLocation(CaveOverhaul.MOD_ID, "canyons_low_y");
        ResourceLocation caves_rloc = new ResourceLocation(CaveOverhaul.MOD_ID, "caves_noise_distribution");

        /*
        Actually add
         */
        BiomeModifications.addCarver(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Carving.AIR,
                ResourceKey.create(Registries.CONFIGURED_CARVER, canyons_rloc));

        BiomeModifications.addCarver(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Carving.AIR,
                ResourceKey.create(Registries.CONFIGURED_CARVER, canyons_low_y_rloc));

        BiomeModifications.addCarver(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Carving.AIR,
                ResourceKey.create(Registries.CONFIGURED_CARVER, caves_rloc));

    }
}
