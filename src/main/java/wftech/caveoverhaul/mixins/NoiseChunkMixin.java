package wftech.caveoverhaul.mixins;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.WorldGenUtils;
import wftech.caveoverhaul.carvertypes.NoisetypeDomainWarp;
import wftech.caveoverhaul.carvertypes.rivers.NURLayerHolder;
import wftech.caveoverhaul.carvertypes.rivers.NURDynamicLayer;
import wftech.caveoverhaul.utils.Globals;
import wftech.caveoverhaul.utils.IMixinHelperNoiseChunk;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin implements IMixinHelperNoiseChunk {

	private DedicatedServer server;
	private NoiseSettings NS;
	private NoiseGeneratorSettings NGS;

	@Inject(method="<init>(ILnet/minecraft/world/level/levelgen/RandomState;IILnet/minecraft/world/level/levelgen/NoiseSettings;Lnet/minecraft/world/level/levelgen/DensityFunctions$BeardifierOrMarker;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at=@At("RETURN"), cancellable=false, remap=true)
	private void constructorMixin(int int1, RandomState randomstate, int int2, int int3, NoiseSettings noiseSettings,
								  DensityFunctions.BeardifierOrMarker beardifier, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker,
								  Blender blender, CallbackInfo ci){

		((NoiseChunkMixin) (Object) this).setNGS(noiseGenSettings);
		((NoiseChunkMixin) (Object) this).setNS(noiseSettings);

	}

	@Inject(method="getInterpolatedState()Lnet/minecraft/world/level/block/state/BlockState;", at=@At("HEAD"), cancellable=true, remap=true)
	private void getInterpolatedStateMixin(CallbackInfoReturnable<BlockState> cir) {
		NoiseChunk thisChunk = (NoiseChunk) (Object) this;

		//boolean isLikelyOverworld = WorldGenUtils.checkIfLikelyOverworld(((NoiseChunkAccessor) this).getNoiseSettings());
		boolean isLikelyOverworld = WorldGenUtils.checkIfLikelyOverworld(((IMixinHelperNoiseChunk) (Object) this).getNGS());
		if(!isLikelyOverworld) {
			return;
		}

		//init layers
		int minY = ((IMixinHelperNoiseChunk) (Object) this).getNGS().noiseSettings().minY();
		Globals.minY = minY;
		NoisetypeDomainWarp.init(minY);

		int x = thisChunk.blockX();
		int y = thisChunk.blockY();
		int z = thisChunk.blockZ();

		int topY = thisChunk.preliminarySurfaceLevel(x, z);
		topY = topY - 8;

		if(y >= topY) {
			return;
		}

		/*
		Patch 1.3.2 - aquifer patch
		 */
		Block original_block_chosen = null;
		if (cir.getReturnValue() != null) {
			original_block_chosen = cir.getReturnValue().getBlock();
		} else {
			original_block_chosen = Blocks.STONE;
		}


		if (original_block_chosen == Blocks.LAVA && y <= (minY + 9)){
			return;
		} else if (original_block_chosen == Blocks.AIR && y <= (minY + 9)){
			return;
		}


		//if (Config.getBoolSetting(Config.KEY_USE_AQUIFER_PATCH) && (original_block_chosen == Blocks.WATER || original_block_chosen == Blocks.LAVA)) {
		if (Config.getBoolSetting(Config.KEY_USE_AQUIFER_PATCH) && (original_block_chosen == Blocks.WATER || original_block_chosen == Blocks.LAVA)) {
			if (NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x, y - 1, z)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x - 1, y, z)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x + 1, y, z)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x, y, z - 1)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x, y, z + 1)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x, y - 1, z)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x - 1, y, z)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x + 1, y, z)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x, y, z - 1)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			} else if (NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x, y, z + 1)) {
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			}
		}

		/*
		End 1.3.2 - aquifer patch
		BUT the original_block_chosen references below in the if statements were part of the aquifer patch
		 */

		NURDynamicLayer riverLayer = NoiseChunkMixinUtils.getRiverLayer(topY, x, y, z);
		BlockState preferredState = null;
		if(riverLayer != null) {
			preferredState = riverLayer.getLiquidType().defaultBlockState();
		} else if(NoiseChunkMixinUtils.shouldSetToStone(topY, x, y, z)) {
			preferredState = Blocks.STONE.defaultBlockState();
		} else if(NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x, y, z)) {
			preferredState = Blocks.AIR.defaultBlockState();
		} else if(NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x, y, z)) {
			preferredState = Blocks.AIR.defaultBlockState();
		}

		if (preferredState != null) {
			Globals.init();
			boolean found_vo = Globals.isVolcanicCavernsLoaded;
			int y_offset = (int) Config.getFloatSetting(Config.KEY_LAVA_OFFSET);
			if (preferredState.isAir() && y <= Globals.minY + y_offset) {
				preferredState = Blocks.LAVA.defaultBlockState();
			}
			cir.setReturnValue(preferredState);
			cir.cancel();
		}
	}

	/*
	For adding variables to NoiseChunk
	 */

	@Override
	public void setNGS(NoiseGeneratorSettings NGS) {
		this.NGS = NGS;
	}

	@Override
	public NoiseGeneratorSettings getNGS() {
		return this.NGS;
	}

	@Override
	public void setNS(NoiseSettings NS) {
		this.NS = NS;
	}

	@Override
	public NoiseSettings getNS() {
		return this.NS;
	}
}