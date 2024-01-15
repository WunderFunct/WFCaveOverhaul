package wftech.caveoverhaul;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegistryObject;

public class InitConfigCarvers {

	public static Set<Holder<ConfiguredWorldCarver<?>>> newCarversList = null;
	public static Set<ResourceLocation> removeCarversList = null;

	public static void init(boolean isReplacement) {

		List<String> newOreFrequencies = new ArrayList<String>();
		removeCarversList = removeCarversList == null ? new HashSet<ResourceLocation>() : removeCarversList;
		newCarversList = newCarversList == null ? new HashSet<Holder<ConfiguredWorldCarver<?>>>() : newCarversList;
		
		removeCarversList.add(Carvers.CANYON.unwrapKey().get().location());
		removeCarversList.add(Carvers.CAVE.unwrapKey().get().location());
		removeCarversList.add(Carvers.CAVE_EXTRA_UNDERGROUND.unwrapKey().get().location());		
		//newCarversList.add((Holder<ConfiguredWorldCarver<?>>) (Object) Init.CFGD.getHolder().get());
		newCarversList.add((Holder<ConfiguredWorldCarver<?>>) (Object) Init.CFGD_CANYON_FROM_VANILLA.getHolder().get());
		newCarversList.add((Holder<ConfiguredWorldCarver<?>>) (Object) Init.CFGD_CANYON_FROM_VANILLA_LOW_Y.getHolder().get());
		newCarversList.add((Holder<ConfiguredWorldCarver<?>>) (Object) Init.MYCELIUM_CAVES_12.getHolder().get());
	}
	
}
