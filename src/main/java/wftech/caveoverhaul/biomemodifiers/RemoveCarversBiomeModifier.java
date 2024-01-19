package wftech.caveoverhaul.biomemodifiers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.virtualpack.JsonConfigCarvers;

public record RemoveCarversBiomeModifier(
		HolderSet<Biome> biomes,
		HolderSet<ConfiguredWorldCarver<?>> carvers,
		GenerationStep.Decoration step) implements BiomeModifier {		

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
		if (phase == BiomeModifier.Phase.REMOVE /*&& this.biomes.contains(biome)*/ && biome.is(BiomeTags.IS_OVERWORLD)) {
			BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
			List<Holder<ConfiguredWorldCarver<?>>> registeredCarvers = generationSettings.getCarvers(Carving.AIR);
			registeredCarvers.removeIf(registeredCarver -> this.carvers.contains(registeredCarver) 
					|| registeredCarver.unwrapKey().get().location().getNamespace().equals("minecraft"));
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return InitBiomeModifiers.BM_REMOVE_CARVERS.get();
	}
}