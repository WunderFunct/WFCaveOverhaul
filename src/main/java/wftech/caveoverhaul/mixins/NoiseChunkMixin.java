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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.WorldGenUtils;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernMiddleLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernMiddleLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer3;
import wftech.caveoverhaul.carvertypes.rivers.*;
import wftech.caveoverhaul.utils.IMixinHelperNoiseChunk;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin  implements IMixinHelperNoiseChunk {

	private DedicatedServer server;
	private NoiseSettings NS;
	private NoiseGeneratorSettings NGS;

	@Inject(method="<init>(ILnet/minecraft/world/level/levelgen/RandomState;IILnet/minecraft/world/level/levelgen/NoiseSettings;Lnet/minecraft/world/level/levelgen/DensityFunctions$BeardifierOrMarker;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at=@At("RETURN"), cancellable=false, remap=true)
	private void constructorMixin(int int1, RandomState randomstate, int int2, int int3, NoiseSettings noiseSettings,
								  DensityFunctions.BeardifierOrMarker beardifier, NoiseGeneratorSettings noiseGenSettings,
								  Aquifer.FluidPicker fluidPicker,
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

		int x = thisChunk.blockX();
		int y = thisChunk.blockY();
		int z = thisChunk.blockZ();

		int topY = thisChunk.preliminarySurfaceLevel(x, z);
		topY = topY - 8;

		if(y >= topY) {
			return;
		}

		/*
		 * RED_STAINED_GLASS = lava
		 * GRAY_STAINED_GLASS = water
		 * YELLOW_STAINED_GLASS = stone
		 * BLACK_STAINED_GLASS = air above rivers
		 */
		/*
		Patch 1.3.2 - aquifer patch
		 */
		Block original_block_chosen = null;
		if (cir.getReturnValue() != null) {
			original_block_chosen = cir.getReturnValue().getBlock();
		} else {
			original_block_chosen = Blocks.STONE;
		}


		if (original_block_chosen == Blocks.LAVA && y <= (-64 + 9)){
			return;
		} else if (original_block_chosen == Blocks.AIR && y <= (-64 + 9)){
			return;
		}


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

		if(NoiseChunkMixinUtils.shouldSetToLava(topY, x, y, z)) {
			cir.setReturnValue(Blocks.LAVA.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToWater(topY, x, y, z)) {
			cir.setReturnValue(Blocks.WATER.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToStone(topY, x, y, z)) {
			cir.setReturnValue(Blocks.STONE.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToAirRivers(topY, x, y, z)) {
			cir.setReturnValue(Blocks.AIR.defaultBlockState());
			cir.cancel();
			return;
		} else if(NoiseChunkMixinUtils.shouldSetToAirCaverns(topY, x, y, z)) {
			cir.setReturnValue(Blocks.AIR.defaultBlockState());
			cir.cancel();
			return;
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