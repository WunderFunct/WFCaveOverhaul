package wftech.caveoverhaul;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wftech.caveoverhaul.carvertypes.InitCarverTypes;

@Mod.EventBusSubscriber(modid = "cavesrevisited")
public class WorldEvents {
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
    public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
		
		List<Holder<ConfiguredWorldCarver<CarverConfiguration>>> carversToRemove = new ArrayList<Holder<ConfiguredWorldCarver<CarverConfiguration>>>();
		carversToRemove.add((Holder<ConfiguredWorldCarver<CarverConfiguration>>) (Object) Carvers.CANYON);
		carversToRemove.add((Holder<ConfiguredWorldCarver<CarverConfiguration>>) (Object) Carvers.CAVE);
		carversToRemove.add((Holder<ConfiguredWorldCarver<CarverConfiguration>>) (Object) Carvers.CAVE_EXTRA_UNDERGROUND);
		
		for(Holder<ConfiguredWorldCarver<CarverConfiguration>> carverToRemove: carversToRemove) {
			int index = 0;
			int foundIndex = -1;
			for(Holder<ConfiguredWorldCarver<?>> activeCarver: event.getGeneration().getCarvers(Carving.AIR)) {
				if(carverToRemove.unwrapKey().get().location().toString().equals(activeCarver.unwrapKey().get().location().toString())) {
					foundIndex = index;
					break;
				}
				index += 1;
			}
			if(foundIndex >= 0) { event.getGeneration().getCarvers(Carving.AIR).remove(foundIndex); }
		}

		event.getGeneration().addCarver(Carving.AIR, InitCarverTypes.MYCELIUM_CAVES_12.getHolder().get());
		event.getGeneration().addCarver(Carving.AIR, InitCarverTypes.CFGD_CANYON_FROM_VANILLA.getHolder().get());
		event.getGeneration().addCarver(Carving.AIR, InitCarverTypes.CFGD_CANYON_FROM_VANILLA_LOW_Y.getHolder().get());
	}
}