package wftech.caveoverhaul.carvertypes;

import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.utils.HashCache;
import wftech.caveoverhaul.utils.IntPair;
import wftech.caveoverhaul.utils.Settings;

//NC stands for NoiseCave
public class NCLogic {
    private final HashCache<IntPair, Float> cacheYLevel = new HashCache<IntPair, Float>(10000);
    private final HashCache<IntPair, Float> cacheCaveHeight = new HashCache<IntPair, Float>(10000);


    private final float minY;
    private final float maxY;
    private final FastNoiseLite caveYNoise;
    private final FastNoiseLite caveSizeNoise;

    public static int MAX_CAVE_SIZE_Y = Settings.MAX_CAVE_SIZE_Y;

    public NCLogic(float minY, float maxY, FastNoiseLite caveYNoise, FastNoiseLite caveSizeNoise){
        this.minY = minY;
        this.maxY = maxY;
        this.caveYNoise = caveYNoise;
        this.caveSizeNoise = caveSizeNoise;
    }

    public float getCachedYLevel(int x, int z) {
        //Pair<Integer, Integer> keyorig = Pair.of((int) x, (int) z);
        IntPair key = new IntPair(x, z);
        //return cacheYLevel.get(key, () -> this.calcYLevel(x, z));
        return this.calcYLevel(x, z);
    }

    public float getCachedCaveHeight(int x, int z) {
        //Pair<Integer, Integer> key = Pair.of((int) x, (int) z);
        IntPair key = new IntPair(x, z);
        //return cacheCaveHeight.get(key, () -> this.calcHeight(x, z));
        return this.calcHeight(x, z);
    }

    private int getCaveY(float noiseValue) {
        return (int) (((this.maxY - this.minY) * noiseValue) + this.minY);
    }

    private float calcYLevel(int x, int z){
        float rawNoiseY = getCaveYNoise(x, z);
        rawNoiseY = NCDynamicLayer.norm(rawNoiseY);
        //rawNoiseY = rawNoiseY > 1 ? 1 : (rawNoiseY < 0 ? 0 : rawNoiseY);
        rawNoiseY = Math.max(0, rawNoiseY);
        rawNoiseY = Math.min(1, rawNoiseY);
        int caveY = getCaveY(rawNoiseY);

        return caveY;
    }

    //Used
    private float getCaveYNoise(int x, int z) {
        return this.caveYNoise.GetNoise(x, z);
    }

    //Used
    private float getCaveThicknessNoise(int x, int z) {
        return this.caveSizeNoise.GetNoise(x, z);
    }

    private float calcHeight(int x, int z){

        float caveHeightNoise = getCaveThicknessNoise(x, z);
        int caveHeight = 0;
        caveHeightNoise = ((1f + caveHeightNoise) / 2f) * (float) MAX_CAVE_SIZE_Y;
        float caveHeightNoiseSquished = ySquish(caveHeightNoise);
        caveHeight = (int) (caveHeightNoiseSquished * MAX_CAVE_SIZE_Y);

        return caveHeight;
    }

    public static float ySquish(float noiseHeight) {
        float caveOffset = (MAX_CAVE_SIZE_Y) / 2f; //(float)MAX_CAVE_SIZE_Y/4f; //if 32, becomes 8. Noise is usually a normal distribution with the mean being MAX/2.
        float k = 2f; //1f = 8 tiles from 1 to 0, 2f = 4 tiles, 16f for an outgoing range of [0, 1]
        //Use https://www.desmos.com/calculator
        //desmos equation: y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(\left(-x\ +\ 32\right)\right)}}
        int dist = 2 + 1; //2f = 2, 4f = 1, 1f = 8, 3f = 1.5?, then add a +1 to account for edge squish weirdness
        if (noiseHeight > caveOffset + dist || noiseHeight < caveOffset - dist) {
            return 0f;
        }

        return 1f - (float) (1f / (1f + Math.exp(k * (-noiseHeight + (caveOffset)))));
    }
}
