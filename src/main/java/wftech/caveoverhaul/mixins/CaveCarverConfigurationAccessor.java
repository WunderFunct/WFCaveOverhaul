package wftech.caveoverhaul.mixins;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CaveCarverConfiguration.class)
public interface CaveCarverConfigurationAccessor {

    @Accessor("floorLevel")
    public FloatProvider getFloorLevel();
}
