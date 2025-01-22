package wftech.caveoverhaul.utils;

import net.minecraft.world.level.block.Blocks;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernBottomLayerAbsolute;
import wftech.caveoverhaul.carvertypes.NoiseCavernMiddleLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernMiddleLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer1;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer2;
import wftech.caveoverhaul.carvertypes.NoiseCavernTopLayer3;
import wftech.caveoverhaul.carvertypes.rivers.*;

public class NoiseChunkMixinUtils {

	public static boolean shouldSetToWater(int topY, int x, int y, int z) {

		if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Water.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer5_Water.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer6_Water.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer7_Water.INSTANCE.isWater(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer8_Water.INSTANCE.isWater(x, y, z)) {
			return true;
		}

		return false;
	}

	public static boolean shouldSetToLava(int topY, int x, int y, int z) {

		if(y <= -55 && shouldSetToAirCaverns(topY, x, y, z)) {
			return true;
		}

		if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isLava(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isLava(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isLava(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isLava(x, y, z)) {
			return true;
		}

		return false;

	}

	public static boolean shouldSetToStone(int topY, int x, int y, int z) {

		if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Water.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer5_Water.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer6_Water.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer7_Water.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer8_Water.INSTANCE.isBelowRiverSupport(x, y, z)) {
			return true;
		}

		if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Water.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer5_Water.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer6_Water.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer7_Water.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer8_Water.INSTANCE.isBelowWaterfallSupport(x, y, z)) {
			return true;
		}

		if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Water.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer5_Water.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer6_Water.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer7_Water.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer8_Water.INSTANCE.isBoundary(x, y, z, true)) {
			return true;
		}

		return false;
	}

	public static boolean shouldSetToAirRivers(int topY, int x, int y, int z) {
		if(NoiseUndergroundRiver_Layer1_Lava1.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer1_Lava2.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer2_Lava.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Lava.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer3_Water.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water1.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer4_Water2.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer5_Water.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer6_Water.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer7_Water.INSTANCE.isAir(x, y, z)) {
			return true;
		} else if(NoiseUndergroundRiver_Layer8_Water.INSTANCE.isAir(x, y, z)) {
			return true;
		}

		return false;
	}

	public static boolean shouldSetToAirCaverns(int topY, int x, int y, int z) {

		if( NoiseCavernBottomLayer1.shouldCarve(x, y, z) ) {
			return true;
		} else if( NoiseCavernBottomLayer2.shouldCarve(x, y, z) ) {
			return true;
		} else if( NoiseCavernBottomLayerAbsolute.shouldCarve(x, y, z) ) {
			return true;
		} else if( NoiseCavernMiddleLayer1.shouldCarve(x, y, z) ) {
			return true;
		} else if( NoiseCavernMiddleLayer2.shouldCarve(x, y, z) ) {
			return true;
		} else if( y < topY && NoiseCavernTopLayer1.shouldCarve(x, y, z) ) {
			return true;
		} else if( y < topY && NoiseCavernTopLayer2.shouldCarve(x, y, z) ) {
			return true;
		} else if( y < topY && NoiseCavernTopLayer3.shouldCarve(x, y, z) ) {
			return true;
		}

		return false;
	}

	public static boolean isAirBlock(int x, int y, int z){
		if(NoiseChunkMixinUtils.shouldSetToAirRivers(9999, x, y, z)) {
			return true;
		} else if(NoiseChunkMixinUtils.shouldSetToAirCaverns(9999, x, y, z)) {
			return true;
		}
		return false;
	}

	public static boolean isStoneBlock(int x, int y, int z){
		if(NoiseChunkMixinUtils.shouldSetToStone(9999, x, y, z)) {
			return true;
		}
		return false;
	}
}