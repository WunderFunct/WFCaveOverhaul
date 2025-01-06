package wftech.caveoverhaul.carvertypes;

import net.minecraft.core.BlockPos;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.utils.FloatPos;
import wftech.caveoverhaul.utils.Settings;

//NC stands for NoiseCave
public class NCDynamicLayer {

    public static float norm(float f) {
        return (1f + f) / 2f;
    }

    public static int MAX_CAVE_SIZE_Y = Settings.MAX_CAVE_SIZE_Y;

    public FastNoiseLite caveSizeNoise = null;
    public FastNoiseLite caveYNoise = null;
    public FastNoiseLite caveStructureNoise = null;
    private NCLogic cache = null;


    /*
     * -64 to 0, doubling up to expand the amount of fun caves near the bottom
     */
    public float minY = -64;
    public float maxY = 0;

    public NCDynamicLayer(int minY, int maxY, FastNoiseLite caveThicknessMap, FastNoiseLite caveYNoise, FastNoiseLite caveStructureNoise) {
        this.minY = minY;
        this.maxY = maxY;

        this.caveSizeNoise = caveThicknessMap;
        this.caveYNoise = caveYNoise;
        this.caveStructureNoise = caveStructureNoise;

        this.cache = new NCLogic(minY, maxY, caveYNoise, caveThicknessMap);
    }

    public boolean isInYRange(int y) {
        return y <= (maxY + MAX_CAVE_SIZE_Y) && y >= minY;
    }

    //REMOVE
    public float getCaveThicknessNoise(int x, int z) {
        return caveSizeNoise.GetNoise(x, z);
    }

    //Used
    public float getCaveYNoise(int x, int z) {
        return caveYNoise.GetNoise(x, z);
    }

    //Used
    public float getWarpedNoise(float x, float y, float z) {
        FloatPos fpos = NoisetypeDomainWarp.getWarpedPosition(x, y, z);
        return caveStructureNoise.GetNoise(fpos.x, fpos.y, fpos.z);
    }

    public boolean shouldCarve(float x, float y, float z) {
        /*
         * Put in subclass (copy and paste)
         */
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        int earlyXPos = (int) x;
        int earlyZPos = (int) z;

        /*
        Eyelets are so we phase in and out of where caverns can be drawn, then the actual check occurs.
        Think about it like sketching out activity zones. If we're in an activity zone, we then check the activity
        itself. Else, do nothing.
         */
        /*
        float caveHeightNoise = getCaveThicknessNoise(earlyXPos, earlyZPos);
        int caveHeight = 0;
        caveHeightNoise = ((1f + caveHeightNoise) / 2f) * (float) MAX_CAVE_SIZE_Y;
        float caveHeightNoiseSquished = ySquish(caveHeightNoise);
        caveHeight = (int) (caveHeightNoiseSquished * MAX_CAVE_SIZE_Y);

         */
        int caveHeight = (int) this.cache.getCachedCaveHeight(earlyXPos, earlyZPos);
        if(caveHeight <= 0) {
            return false;
        }

        /*
        //Actual noise check for the cave itself
        float rawNoiseY = getCaveYNoise(earlyXPos, earlyZPos);
        rawNoiseY = NCDynamicLayer.norm(rawNoiseY);
        //rawNoiseY = rawNoiseY > 1 ? 1 : (rawNoiseY < 0 ? 0 : rawNoiseY);
        rawNoiseY = Math.max(0, rawNoiseY);
        rawNoiseY = Math.min(1, rawNoiseY);
        int caveY = getCaveY(rawNoiseY);
         */
        int caveY = (int) this.cache.getCachedYLevel(earlyXPos, earlyZPos);


        return shouldCarveBasedOnHeight(x, y, z, caveHeight, caveY);
    }

    // REMOVE
    public static float ySquish(float noiseHeight) {
        float caveOffset = ((float) MAX_CAVE_SIZE_Y) / 2f; //(float)MAX_CAVE_SIZE_Y/4f; //if 32, becomes 8. Noise is usually a normal distribution with the mean being MAX/2.
        float k = 2f; //1f = 8 tiles from 1 to 0, 2f = 4 tiles, 16f for an outgoing range of [0, 1]
        //Use https://www.desmos.com/calculator
        //desmos equation: y\ =\ 1\ -\ \frac{1}{1\ +\ e^{\left(\left(-x\ +\ 32\right)\right)}}
        int dist = 2 + 1; //2f = 2, 4f = 1, 1f = 8, 3f = 1.5?, then add a +1 to account for edge squish weirdness
        if (noiseHeight > caveOffset + dist || noiseHeight < caveOffset - dist) {
            return 0f;
        }

        return 1f - (float) (1f / (1f + Math.exp(k * (-noiseHeight + (caveOffset)))));

    }

    public int getCaveY(float noiseValue) {
        return (int) (((maxY - minY) * noiseValue) + minY);
    }

    public float getNoiseThreshold(float x, float z) {
        return 0.15f;
    }

    public boolean shouldCarveBasedOnHeight(float x, float y, float z, int caveHeight, int caveY) {

        int y_adj = (int) y;
        int yPos = (int) y;

        if(yPos < caveY || yPos > caveY + caveHeight) {
            return false;
        }

        int xPos = (int) x;
        int zPos = (int) z;

        float noiseFound = getWarpedNoise(xPos, yPos*2, zPos);

        return noiseFound > getNoiseThreshold(xPos, zPos);
    }

}