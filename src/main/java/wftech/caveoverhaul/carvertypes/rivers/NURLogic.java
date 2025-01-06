package wftech.caveoverhaul.carvertypes.rivers;

import net.minecraft.core.BlockPos;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.utils.HashCache;

public class NURLogic {

    private FastNoiseLite domainWarp = null;
    public FastNoiseLite noiseIsLiquid = null;
    public FastNoiseLite noiseShouldCarveBase = null;
    public FastNoiseLite noiseYLevelBase = null;

    //overhead > speed improvements :(
    //private final HashCache<Long, Float> cacheIsLiquid = new HashCache<Long, Float>(10000);
    //private final HashCache<Long, Float> cacheYLevel = new HashCache<Long, Float>(10000);

    public NURLogic(FastNoiseLite noiseIsLiquid, FastNoiseLite noiseShouldCarveBase, FastNoiseLite noiseYLevelBase) {
        this.noiseIsLiquid = noiseIsLiquid;
        this.noiseShouldCarveBase = noiseShouldCarveBase;
        this.noiseYLevelBase = noiseYLevelBase;
    }

    public float getCachedYLevel(int x, int y, int z, FastNoiseLite noiseGenerator) {
        Long key = BlockPos.asLong(x, y, z);
        //return .get(key, () -> noiseGenerator.GetNoise(x, y, z));
        return noiseGenerator.GetNoise(x, y, z);
    }

    public float getCaveDetailsNoise2D(int x, int z) {
        //return cacheIsLiquid.get(BlockPos.asLong(x, 0, z), () -> noiseIsLiquid.GetNoise(x, z));
        return noiseIsLiquid.GetNoise(x, z);
    }

    public float getCaveYNoise(int x, int z) {
        //return cacheYLevel.get(BlockPos.asLong(x, 0, z), () -> noiseYLevelBase.GetNoise(x, z));
        return noiseYLevelBase.GetNoise(x, z);
    }

    /*
    OOB test, was never properly implemented.
    Sratch it?
     */
    public float getShouldCarveNoise(int x, int z) {
        return noiseShouldCarveBase.GetNoise(x, z);
    }
}
