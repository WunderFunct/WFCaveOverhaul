package wftech.caveoverhaul;

import net.minecraft.world.level.levelgen.NoiseSettings;

public class WorldGenUtils {
	
	public static boolean checkIfLikelyOverworld(NoiseSettings settings) {		
		boolean rightHeight = settings.height() == 384;
		boolean rightDepth = settings.minY() == -64;
		boolean rightRatioVertical = settings.noiseSizeHorizontal() == 1;
		//boolean rightRatioHorizontal = settings.noiseSizeVertical() == 2;
		
		return rightHeight && rightDepth && rightRatioVertical;
	}

}
