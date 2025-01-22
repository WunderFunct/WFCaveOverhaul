package wftech.caveoverhaul.utils;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;

public interface IMixinHelperNoiseChunk {

    void setNGS(NoiseGeneratorSettings NGS);
    NoiseGeneratorSettings getNGS();

    void setNS(NoiseSettings NS);
    NoiseSettings getNS();

}