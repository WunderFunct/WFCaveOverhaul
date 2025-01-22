package wftech.caveoverhaul.mixins;

import cpw.mods.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.levelgen.Aquifer.NoiseBasedAquifer;

import wftech.caveoverhaul.utils.IMixinHelperNoiseChunk;
import wftech.caveoverhaul.utils.LazyLoadingSafetyWrapper;
import wftech.caveoverhaul.utils.MiscUtils;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.WorldGenUtils;

@Mixin(NoiseBasedAquifer.class)
public class NoiseBasedAquiferMixin {

	@Inject(method="shouldScheduleFluidUpdate()Z", at=@At("HEAD"), remap=true, cancellable=true)
	private void scheduleFluidUpdateMixin(CallbackInfoReturnable<Boolean> cir) {
		if(CaveOverhaul.ENABLE_MULTILAYER_RIVERS) {
			cir.setReturnValue(true);
			return;
		}
	}
	@Inject(method="computeSubstance(Lnet/minecraft/world/level/levelgen/DensityFunction$FunctionContext;D)Lnet/minecraft/world/level/block/state/BlockState;",
			at = @At("RETURN"), cancellable=true, remap=true)
	private void computeSubstanceMixin(DensityFunction.FunctionContext df, double unkDouble, CallbackInfoReturnable<BlockState> cir) {
		//don't forget to check if I'm in the overworld or not!

		//boolean USE_AQUIFER_PATCH = Config.settings.get(Config.KEY_USE_AQUIFER_PATCH) == 1f;
		boolean USE_AQUIFER_PATCH = Config.getBoolSetting(Config.KEY_USE_AQUIFER_PATCH);
		if(!USE_AQUIFER_PATCH) {
			return;
		}

		//cheeselands, funnylands, skylands...
		//it's all the same!
		boolean CHEESE_LANDS_ENABLED = false;
		if(!CHEESE_LANDS_ENABLED) {
			if (cir.getReturnValue() == null || !(cir.getReturnValue().is(Blocks.WATER) || cir.getReturnValue().is(Blocks.LAVA))) {
				return;
			}
		}

		if (df == null) {
			return;
		}

		NoiseChunk thisChunk = ((AquiferAccessor) (Object) this).getNoiseChunk();

		//boolean isLikelyOverworld = WorldGenUtils.checkIfLikelyOverworld( ((NoiseChunkAccessor) thisChunk).getNoiseSettings() );
		boolean isLikelyOverworld = WorldGenUtils.checkIfLikelyOverworld(((IMixinHelperNoiseChunk) (Object) thisChunk).getNGS());
		if(!isLikelyOverworld) {
			return;
		}

		try {
			int x = df.blockX();
			int y = df.blockY();
			int z = df.blockZ();

			if (y <= (-64 + 9) && cir.getReturnValue().getBlock() == Blocks.LAVA) {
				return;
			}

			int topY = thisChunk.preliminarySurfaceLevel(x, z);
			topY = topY - 8;

			if(y >= topY) {
				return;
			}

			boolean is_air_w = NoiseChunkMixinUtils.isAirBlock(x + 1, y, z);
			boolean is_air_e = NoiseChunkMixinUtils.isAirBlock(x - 1, y, z);
			boolean is_air_n = NoiseChunkMixinUtils.isAirBlock(x, y, z + 1);
			boolean is_air_s = NoiseChunkMixinUtils.isAirBlock(x, y, z - 1);
			boolean is_air_d = NoiseChunkMixinUtils.isAirBlock(x, y - 1, z);

			if (is_air_d || is_air_w || is_air_e || is_air_n || is_air_s) {

				// Check if the aquifer replacement should even run...
				// Doing the check here to prevent speed issues

				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				if (server != null) {
					Level level = null;

					if (MiscUtils.overworld == null) {
						for (Level tlevel: server.getAllLevels()){

							if (tlevel.dimensionTypeRegistration().unwrap().left().isEmpty()){
								return;
							}

							if (tlevel.dimensionTypeRegistration().unwrap().left().get() == BuiltinDimensionTypes.OVERWORLD){
								level = tlevel;
								break;
							}
						}

						MiscUtils.overworld = level;
					} else {
						level = MiscUtils.overworld;
					}

					if (level == null){
						return;
					}

					if (y >= level.getSeaLevel() - 25) {
						return;
					}
				}
				cir.setReturnValue(Blocks.STONE.defaultBlockState());
				cir.cancel();
			}

		} catch (NullPointerException e){
			//Do nothing, expected to throw under certain circumstances
		}
	}
	// m_207104_(Lnet/minecraft/world/level/levelgen/DensityFunction$FunctionContext;D)Lnet/minecraft/world/level/block/state/BlockState;
}