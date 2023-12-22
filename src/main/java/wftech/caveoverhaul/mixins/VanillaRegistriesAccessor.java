package wftech.caveoverhaul.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.VanillaRegistries;

@Mixin(VanillaRegistries.class)
public interface VanillaRegistriesAccessor {

	@Accessor("BUILDER")
	public static RegistrySetBuilder getBuilder() {
		throw new AssertionError();
	}
}
