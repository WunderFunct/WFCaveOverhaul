package wftech.caveoverhaul.utils;

import wftech.caveoverhaul.carvertypes.*;
import wftech.caveoverhaul.carvertypes.rivers.*;

public class NoiseChunkMixinUtils {

	public static NURDynamicLayer getRiverLayer(int topY, int x, int y, int z) {
		return NURLayerHolder.INSTANCE.getRiverLayer(topY, x, y, z);
		//return ThreadLocalNURLayerHolderManager.getCurrentHolder().getRiverLayer(topY, x, y, z);
	}

	public static boolean shouldSetToStone(int topY, int x, int y, int z) {
		return NURLayerHolder.INSTANCE.shouldSetToStone(topY, x, y, z);
		//return ThreadLocalNURLayerHolderManager.getCurrentHolder().shouldSetToStone(topY, x, y, z);
	}

	public static boolean shouldSetToAirRivers(int topY, int x, int y, int z) {
		return NURLayerHolder.INSTANCE.shouldSetToAirRivers(topY, x, y, z);
		//return ThreadLocalNURLayerHolderManager.getCurrentHolder().shouldSetToAirRivers(topY, x, y, z);
	}

	public static boolean shouldSetToAirCaverns(int topY, int x, int y, int z) {
		return NCLayerHolder.INSTANCE.shouldCarve(x, y, z);
		//return ThreadLocalNCLayerHolderManager.getCurrentHolder().shouldCarve(x, y, z);
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
		return NoiseChunkMixinUtils.shouldSetToStone(9999, x, y, z);
	}
}