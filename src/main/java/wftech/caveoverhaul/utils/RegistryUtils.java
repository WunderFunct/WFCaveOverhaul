package wftech.caveoverhaul.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.commands.Commands;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.RegistrySetBuilder.RegistryStub;
import net.minecraft.core.HolderGetter.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryDataLoader.Loader;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.mixins.VanillaRegistriesAccessor;

public class RegistryUtils {

	public static Map<ResourceKey, Registry> REGISTRIES = new HashMap<ResourceKey, Registry>();
	public static Map<ResourceLocation, String> RESOURCE_LOCATION_STRING_MAP = new HashMap<ResourceLocation, String>();
	public static Map<ResourceLocation, JsonElement> RESOURCE_LOCATION_JSON_MAP = new HashMap<ResourceLocation, JsonElement>();
	
	public static void initRegistryClone() {
		
		if(REGISTRIES.keySet().size() > 0 ) {
			return;
		}
		
		//This flow was copied from various Forge and MC files
		//Creates a private set of registries which is NOT MC's.
		//This registry set should be fully independent.
		
		//Occurs before WorldLoader.load() - During CreateWorldScreen.openFresh(), unknown what the server
		//equivalent is.
		PackRepository packrepository = new PackRepository(new ServerPacksSource());
		WorldLoader.PackConfig packConfig = new WorldLoader.PackConfig(packrepository, WorldDataConfiguration.DEFAULT, false, true);
		WorldLoader.InitConfig initConfig = new WorldLoader.InitConfig(packConfig, Commands.CommandSelection.INTEGRATED, 2);
		
		//WorldLoader.load() snippet
		LayeredRegistryAccess<RegistryLayer> access = new LayeredRegistryAccess<RegistryLayer>(RegistryLayer.VALUES);
        RegistryAccess.Frozen accessFrozen = access.getAccessForLoading(RegistryLayer.WORLDGEN);
        List<RegistryData<?>> registryTypeCodecList = DataPackRegistriesHooks.getDataPackRegistries();
        Map<ResourceKey<?>, Exception> map = new HashMap<>();
        List<Pair<WritableRegistry<?>, Loader>> loadDataTriggers = new ArrayList<Pair<WritableRegistry<?>, Loader>>();
        CloseableResourceManager resourceManager = initConfig.packConfig().createResourceManager().getSecond();

        //RegistryDataLoader.load for below
        for(RegistryData<?> registryDataEntry: registryTypeCodecList) {
        	loadDataTriggers.add(registryDataEntry.create(Lifecycle.stable(), map));
        }
        RegistryInfoLookup infoLookup = RegistryDataLoader.createContext(accessFrozen, loadDataTriggers);

        for(RegistryData<?> registryDataEntry: registryTypeCodecList) {
            attemptLoadIndividualRegistry(Registries.PLACED_FEATURE, infoLookup, resourceManager, registryDataEntry);
            attemptLoadIndividualRegistry(Registries.CONFIGURED_CARVER, infoLookup, resourceManager, registryDataEntry);
        }
        
        loadDataTriggers.forEach(p_255508_ -> {
        	//Rewrite this part -_-
        	((Loader)p_255508_.getSecond()).load(resourceManager, infoLookup);
    	});
        
        loadDataTriggers.forEach(p_258223_ -> {
            Registry registry = (Registry)p_258223_.getFirst();
            REGISTRIES.put(registry.key(), registry);
        });
	}
	
	protected static void attemptLoadIndividualRegistry(
				ResourceKey requestedRegistry, 
				RegistryOps.RegistryInfoLookup infoLookup,
				ResourceManager resourceManager,
				RegistryData registryDatapoint
			) {
		WritableRegistry writableregistry = new MappedRegistry(requestedRegistry, Lifecycle.experimental());
		
		/*
		 * RegistryOps.RegistryInfoLookup p_256369_, 
		 * ResourceManager p_256349_, 
		 * ResourceKey<? extends Registry<E>> p_255792_, 
		 * WritableRegistry<E> p_256211_, 
		 * Decoder<E> p_256232_, 
		 * Map<ResourceKey<?>, Exception> p_255884_
		 */
		String s = ForgeHooks.prefixNamespace(requestedRegistry.location());
		FileToIdConverter filetoidconverter = FileToIdConverter.json(s);
		RegistryOps<JsonElement> registryops = RegistryOps.create(JsonOps.INSTANCE, infoLookup);

		//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] ======= ");
		for(Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
			ResourceLocation resourcelocation = entry.getKey();
			//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] key: " + resourcelocation);
			ResourceKey resourcekey = ResourceKey.create(requestedRegistry, filetoidconverter.fileToId(resourcelocation));
			//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] key 2: " + filetoidconverter.fileToId(resourcelocation));
			Resource resource = entry.getValue();
			/*
			try {
				//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] resource: " + resource.open().toString());
			} catch (IOException e) {
				//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] IOException on resource: ");
				e.printStackTrace();
			}
			*/
			
			try (Reader reader = resource.openAsReader()) {
				//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] reader: " + reader);
				JsonElement jsonelement = JsonParser.parseReader(reader);
				//TODO: mangle minecraft:worldgen/placed_feature/ore_iron_middle.json to become
				//minecraft:ore_iron_middle :)
				String tResourceLocation = resourcelocation.toString();
				int lastPosition = tResourceLocation.split("/").length;
				lastPosition -= 1;
				lastPosition = lastPosition < 0 ? 0 : lastPosition;
				String part1 = tResourceLocation.split(":")[0];
				String part2 = tResourceLocation.split("/")[lastPosition];
				String part3 = part2.split("\\.")[0];
				ResourceLocation shortformRL = new ResourceLocation(part1, part3);
				RESOURCE_LOCATION_STRING_MAP.put(shortformRL, jsonelement.toString());
				RESOURCE_LOCATION_JSON_MAP.put(shortformRL, jsonelement);
				
				
				//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] jsonelement: " + jsonelement);
				//DataResult dataresult = registryDatapoint.elementCodec().parse(registryops, jsonelement);
				//WorldgenRevisited.LOGGER.error("[WorldgenRevisited] dataresult: " + dataresult);
				//if (!net.minecraftforge.common.crafting.conditions.ICondition.shouldRegisterEntry(jsonelement)) {
				//	continue;
				//}
				/*
				requestedRegistry e = dataresult.getOrThrow(false, (p_248715_) -> {
				});
				p_256211_.register(resourcekey, e, resource.isBuiltin() ? Lifecycle.stable() : dataresult.lifecycle());
				*/
			} catch (Exception exception) {
				CaveOverhaul.LOGGER.error("RegistryUtils Major error! " + exception);
				StackTraceElement[] stack = exception.getStackTrace();
				for(StackTraceElement el: stack) {
					CaveOverhaul.LOGGER.error("[Cave Overhaul] -> RegistryUtils " + el);
				}
					//p_255884_.put(resourcekey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", resourcelocation, resource.sourcePackId()), exception));
			}
		}
		
	}
	
	public static Registry getRegistryFromRegistryClone(ResourceKey registry) {
		if(REGISTRIES.size() == 0) {
			initRegistryClone();
		}
		
		return REGISTRIES.get(registry);
	}

	public static RegistryAccess getRegistryAccess() {
		RegistryAccess registries = null;
		if(EffectiveSide.get().isClient()) {
			Level level = LazyLoadingSafetyWrapper.getClientLevel();
			registries = level.registryAccess();
			
		} else {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			registries = server.registryAccess();
			Provider registryProvider = registries.asGetterLookup();
		}
		
		return registries;
	}

	public static <T> Registry<T> getRegistryDirect(ResourceKey<Registry<T>> registry) {
		
		Registry<T> hahahahaha = RegistryLayer.WORLDGEN.createRegistryAccess().compositeAccess().registryOrThrow(registry);
		
		/*
		for(ResourceLocation entry : hahahahaha.keySet()) {
			WorldgenRevisited.LOGGER.debug("[Datapacks Suck] " + entry + " -> " + hahahahaha.get(entry));
		}
		
		for(ResourceLocation entry: BuiltInRegistries.FEATURE.keySet()) {
			WorldgenRevisited.LOGGER.debug("[WorldgenRevisited] Sanity check, " + entry + " -> " + BuiltInRegistries.FEATURE.get(entry));
		}
		
		for(ResourceLocation entry: ForgeRegistries.FEATURES.getKeys()) {
			WorldgenRevisited.LOGGER.debug("[WorldgenRevisited] Sanity check 2, " + entry + " -> " + ForgeRegistries.FEATURES.getHolder(entry));
		}
		
		WorldgenRevisited.LOGGER.debug("[WorldgenRevisited] Attempting to retrieve unfrozen registry entry for key " + registry);
		*/
		
		List<RegistryStub<?>> entryList = VanillaRegistriesAccessor.getBuilder().entries;
		for(RegistryStub<?> entry: entryList) {
			if(entry.key().equals(registry)) {
				//WorldgenRevisited.LOGGER.debug("[WorldgenRevisited] Found matching registry " + entry.key() + " -> " + entry);
				entry.bootstrap();
			} else {
				//WorldgenRevisited.LOGGER.debug("[WorldgenRevisited] Skipping registry " + entry.key() + " -> " + entry);
			}
		}
		
		//RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		
		return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).registryOrThrow(registry);
	}

	public static <T extends Object> Registry<T> getRegistry(ResourceKey<Registry<T>> registry) {
		RegistryAccess registries = getRegistryAccess();
		
		return registries.registryOrThrow(registry);
	}
	
	public static <T extends Object> HolderGetter<T> getHolderGetter(ResourceKey<Registry<T>> registry) {

		HolderGetter holderGetter = null;
		RegistryAccess registries;
		if(EffectiveSide.get().isClient()) {
			Level level = LazyLoadingSafetyWrapper.getClientLevel();
			
			if(level != null) {
				registries = level.registryAccess();
				holderGetter = level.holderLookup(registry);
			} else {
				net.minecraft.core.HolderLookup.Provider hg_test = VanillaRegistries.createLookup();
				holderGetter = hg_test.lookupOrThrow(registry);
			}
			
		} else {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			registries = server.registryAccess();
			Provider registryProvider = registries.asGetterLookup();
			holderGetter = registryProvider.lookupOrThrow(registry);	
		}
		
		return holderGetter;
	}
}
