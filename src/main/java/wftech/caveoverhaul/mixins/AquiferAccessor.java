package wftech.caveoverhaul.mixins;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public interface AquiferAccessor {

    @Accessor("noiseChunk")
    public NoiseChunk getNoiseChunk();

}