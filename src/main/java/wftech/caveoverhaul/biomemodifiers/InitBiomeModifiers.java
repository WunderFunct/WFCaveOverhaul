package wftech.caveoverhaul.biomemodifiers;


import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import wftech.caveoverhaul.CaveOverhaul;

//https://forge.gemwire.uk/wiki/Biome_Modifiers
//https://forums.minecraftforge.net/topic/116895-1192-cannot-get-custom-biome-modifier-to-work/
public class InitBiomeModifiers {

	public static void registerDeferred(IEventBus eventBus) {
		BIOME_MODIFIER_SERIALIZERS.register(eventBus);
	}

	public static DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
			DeferredRegister.create(NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS, CaveOverhaul.MOD_ID);


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AddCarversBiomeModifier>> BM_ADD_CARVERS = BIOME_MODIFIER_SERIALIZERS.register("add_carver",
			() -> RecordCodecBuilder.mapCodec(
			        builder -> builder.group(
							ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(AddCarversBiomeModifier::carvers)
			        ).apply((Applicative) builder, AddCarversBiomeModifier::new)));

	/*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final DeferredHolder<MapCodec<RemoveCarversBiomeModifier>> BM_REMOVE_CARVERS = BIOME_MODIFIER_SERIALIZERS.register("remove_carver",
			() -> RecordCodecBuilder.mapCodec(
			        builder -> builder.group(
			            ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(RemoveCarversBiomeModifier::carvers)
			        ).apply((Applicative) builder, RemoveCarversBiomeModifier::new)));
	*/


}
