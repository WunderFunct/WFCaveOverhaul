package wftech.caveoverhaul.virtualpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import wftech.caveoverhaul.CaveOverhaul;

/*
 * What does a resourcepack look like?
 * 
 * The name is the namespace of the mod.
 * The root resource is.... idk lol
 */
public class VirtualPackResources extends AbstractPackResources{
	
	public static Map<ResourceLocation, IoSupplier<InputStream>> STREAM_MAP = new HashMap<ResourceLocation, IoSupplier<InputStream>>();
	public static Map<ResourceLocation, IoSupplier<InputStream>> STREAM_MAP_CARVER = new HashMap<ResourceLocation, IoSupplier<InputStream>>();

	public String vpName;
	//private Map<ResourceLocation, IoSupplier<InputStream>> streamMap = new HashMap<ResourceLocation, IoSupplier<InputStream>>();
	
	public VirtualPackResources(String vpName, boolean isBuiltIn) {
		super(vpName, isBuiltIn);
		vpName = vpName;
	}
	
	public void addResourceLocation(ResourceLocation resourceLocation, String rawJson) {
		VirtualPackResources.STREAM_MAP.put(resourceLocation, new MemoryBasedIoSupplier(rawJson, resourceLocation.toString()));
	}
	
	public void addResourceLocation(ResourceLocation resourceLocation, JsonElement rawJson) {
		VirtualPackResources.STREAM_MAP.put(resourceLocation, new MemoryBasedIoSupplier(rawJson.toString(), resourceLocation.toString()));
	}

	@Override
	public IoSupplier<InputStream> getRootResource(String... p_252049_) {
		//Null in DelegatingPack, so null here?
		return null;
	}

	@Override
	public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
		return VirtualPackResources.STREAM_MAP.get(resourceLocation);
	}

	//Is this purely for debug purposes?
	@Override
	public void listResources(PackType p_10289_, String string1, String string2, ResourceOutput consumer) {
		if(string2.equals("worldgen/configured_carver")) {
		
			//placed_feature
			for(ResourceLocation key: STREAM_MAP_CARVER.keySet()) {
				consumer.accept(key, STREAM_MAP_CARVER.get(key));
			}
			
		} else if (string2.equals("worldgen/placed_feature")) {
		
			//placed_feature
			for(ResourceLocation key: STREAM_MAP.keySet()) {
				consumer.accept(key, STREAM_MAP.get(key));
			}
	        
		}
	}

	@Override
	public Set<String> getNamespaces(PackType p_10283_) {
		//What in the world, other MC code implies it's for relevant modpacks. What. It's literally just
		//the mod's name!
		Set<String> setName = new TreeSet<String>();
		setName.add(this.packId());
		return setName;
		
	}

	@Override
	public void close() {
		for(ResourceLocation key: VirtualPackResources.STREAM_MAP.keySet()) {
			IoSupplier<InputStream> stream = VirtualPackResources.STREAM_MAP.get(key);
			try {
				stream.get().close();
			} catch (IOException e) {
				CaveOverhaul.LOGGER.error("IOException: Attempting to close VirtualPackResources IOStream under key " + key + ", failed.");
				e.printStackTrace();
			}
		}
	}

}
