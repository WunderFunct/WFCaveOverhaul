package wftech.caveoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;

/*
 * Override either Mapped.mapAll or Mapped.create
 * 
 * Also, the density function visitor calls mapAll :O
 * 
 * So maybe inject into mapAll to see what visitor is being called, then inject into that with
 * a fix?
 */

@Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions.Mapped")
public class DensityFunctionsMixin {

}
