package wftech.caveoverhaul.biomemodifiers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.virtualpack.JsonConfigCarvers;

public record AddCarversBiomeModifier(HolderSet<ConfiguredWorldCarver<?>> carvers) implements BiomeModifier {
	
	public static List<Holder<ConfiguredWorldCarver>> POSTGEN_ADD_ULTRALARGE_NOISE_FEATURES = new ArrayList<>();

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {

		if (phase == BiomeModifier.Phase.ADD && biome.is(BiomeTags.IS_OVERWORLD)) {
			BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
			
			for(Holder<ConfiguredWorldCarver<?>> holder: this.carvers()) {
				generationSettings.addCarver((Holder<ConfiguredWorldCarver<?>>)holder);
			}
		}
	}

	@Override
	public MapCodec<? extends BiomeModifier> codec() {
		//return InitBiomeModifiers.BM_ADD_CARVERS.get();
		return InitBiomeModifiers.BM_ADD_CARVERS.get();
	}
	
}