package wftech.caveoverhaul.virtualpack;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import wftech.caveoverhaul.CaveOverhaul;

public class VirtualRepositorySource implements RepositorySource {
	
	public Pack.ResourcesSupplier getPackResourcesLambda() {
		return x -> {
			return new VirtualPackResources(CaveOverhaul.MOD_ID, false);
		};
	}
	
	@Override
	public void loadPacks(Consumer<Pack> consumer) {
		Pack.ResourcesSupplier resourceSupplierBuilder = this.getPackResourcesLambda();
		Pack.Info info = new Pack.Info(Component.literal("Cave Overhaul Custom Features"), 15, FeatureFlagSet.of());
		
		Pack pack_server = Pack.create(
			"caveoverhaul_custom_features_server", 
			info.description(), 
			true, 
			resourceSupplierBuilder, 
			info, 
			PackType.SERVER_DATA, 
			Pack.Position.TOP, //or should it be bottom?
			true, 
			PackSource.DEFAULT);
		
		Pack pack_client = Pack.create(
			"caveoverhaul_custom_features_client", 
			info.description(), 
			true, 
			resourceSupplierBuilder, 
			info, 
			PackType.CLIENT_RESOURCES, 
			Pack.Position.TOP, //or should it be bottom?
			true, 
			PackSource.DEFAULT);

		consumer.accept(pack_server);
		//consumer.accept(pack_client);
	}

}
