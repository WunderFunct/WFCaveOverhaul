package wftech.caveoverhaul.mixins;

import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CaveWorldCarver.class)
public interface CaveWorldCarverAccessor {

    @Invoker("shouldSkip")
    public static boolean shouldSkip(double d, double e, double f, double g) {
        throw new AssertionError();
    };

}
