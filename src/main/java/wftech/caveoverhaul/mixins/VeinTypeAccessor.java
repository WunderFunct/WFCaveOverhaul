package wftech.caveoverhaul.mixins;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.OreVeinifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OreVeinifier.VeinType.class)
public interface VeinTypeAccessor {


    //final int minY;, maxY
    @Accessor("minY")
    public int minY();

    //final int minY;, maxY
    @Accessor("maxY")
    public int maxY();
}
