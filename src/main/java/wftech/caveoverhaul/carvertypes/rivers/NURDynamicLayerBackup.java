package wftech.caveoverhaul.carvertypes.rivers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.Config;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.FractalType;
import wftech.caveoverhaul.fastnoise.FastNoiseLite.NoiseType;
import wftech.caveoverhaul.utils.Globals;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;
import wftech.caveoverhaul.utils.Settings;

//NUR stands for Noise Underground River
public class NURDynamicLayerBackup {

    public static int MAX_CAVE_SIZE_Y = Settings.MAX_CAVE_SIZE_Y;
    public static float NOISE_CUTOFF_RIVER = 0.92f;
    public static float NOISE_CUTOFF_RIVER_NON_WARPED = 0.75f;
    public int seedOffset = 0;
    private int min_y = 0;
    private Block blockType = Blocks.WATER;

    //public static FastNoiseLite noise = null;
    //public static FastNoiseLite yNoise = null;
    //public static FastNoiseLite caveSizeNoise = null;
    private FastNoiseLite domainWarp = null;
    public FastNoiseLite mNoise = null;
    public FastNoiseLite mNoiseShouldCarveBase = null;
    public FastNoiseLite mNoiseYLevelBase = null;

    //public static NoiseUndergroundRiver INSTANCE = new NURDynamicLayer();
    public NURDynamicLayerBackup(Block blockType, int min_y, int seedOffset) {
        super();
        this.blockType = blockType;
        this.min_y = min_y;
        this.seedOffset = seedOffset;
    }

    public NURDynamicLayerBackup() {}

    /*
     * Edit code below
     *
     * Layer 1 = -64 to -48
     * 2 = -48 to -32 * Lava x1
     * 3 = -32 to -16 * Mixed
     * -16 to 0 * Water x2
     * 0 to 16 * Water x1
     * 16 to 32 * Water x1
     * 32 to 48 * Water x1
     * 48 to 64
     */

    protected int getCaveY(float noiseValue) {
        //40 is the midpoint
        float min = this.min_y;
        float max = (this.min_y) + 8; //3

        if (this.blockType == Blocks.WATER) {
            if(Config.getBoolSetting(Config.KEY_WATER_RIVER_FLAT)){
                return (int) min;
            }
        } else if (this.blockType == Blocks.LAVA) {
            if(Config.getBoolSetting(Config.KEY_LAVA_RIVER_FLAT)){
                return (int) min;
            }
        }

        float diffSize = max - min;
        return (int) (noiseValue * (diffSize)) + (int) min;
    }

    public Block getLiquidType() {
        return this.blockType;
    }

    //Lava by default (in a mixed set) -OR- <entry>2 -> > 0f. Else, < 0f.
    protected boolean isOutOfBounds(int x, int z) {
        float shouldCarveNoise = this.getShouldCarveNoise(x, z);
        return shouldCarveNoise > 0f;
    }

    protected void initNoise() {

        FastNoiseLite tnoise = new FastNoiseLite();
        tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + seedOffset + 2);
        tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
        tnoise.SetFrequency(0.003f); //CHANGED was 0.003
        tnoise.SetFractalType(FractalType.Ridged);
        tnoise.SetFractalOctaves(1);

        mNoise = tnoise;
    }

    protected void initNoiseYLevel() {

        FastNoiseLite tnoise = new FastNoiseLite();
        tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + seedOffset);
        tnoise.SetNoiseType(NoiseType.OpenSimplex2); //SimplexFractal
        tnoise.SetFrequency(0.002f);

        mNoiseYLevelBase = tnoise;
    }

    protected void initShouldCarveNoise() {

        FastNoiseLite tnoise = new FastNoiseLite();
        tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed() + seedOffset + 1);
        tnoise.SetNoiseType(NoiseType.OpenSimplex2);
        tnoise.SetFrequency(0.0015f);
        mNoiseShouldCarveBase = tnoise;
    }

    protected void initDomainWarp() {

        FastNoiseLite tnoise = new FastNoiseLite();
        tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed());
        tnoise.SetNoiseType(NoiseType.OpenSimplex2);
        tnoise.SetFrequency(0.025f);
        tnoise.SetFractalLacunarity(1.1f);
        tnoise.SetFractalGain(1.6f);
        domainWarp = tnoise;
    }

    protected float getCaveDetailsNoise2D(float x, float z) {
        if(mNoise == null) {
            initNoise();
        }

        return mNoise.GetNoise(x, z);
    }

    protected float getCaveYNoise(int x, int z) {
        if(mNoiseYLevelBase == null) {
            initNoiseYLevel();
        }

        return mNoiseYLevelBase.GetNoise(x, z);
    }

    protected float getShouldCarveNoise(int x, int z) {
        if(mNoiseShouldCarveBase == null) {
            initShouldCarveNoise();
        }

        return mNoiseShouldCarveBase.GetNoise(x, z);
    }




    public boolean enableRiver() {
        Block preferredBlock = this.getLiquidType();
        if(preferredBlock == Blocks.LAVA && !Config.getBoolSetting(Config.KEY_LAVA_RIVER_ENABLE)) {
            return false;
        } else if (preferredBlock == Blocks.WATER && !Config.getBoolSetting(Config.KEY_WATER_RIVER_ENABLE)) {
            return false;
        }

        return true;
    }

    public float getNoise2D(int xPos, int zPos) {
        return this.getCaveDetailsNoise2D(xPos, zPos);
    }

    protected float getWarpedNoise(int xPos, int zPos) {

        if(domainWarp == null) {
            initDomainWarp();
        }

        float warpX = xPos;
        float warpZ = zPos;
        for(int i = 0; i < 2; i++) {
            //CHANGED
            //Not applying an offset to warpX is intentional.
            //The location for warpX can be anywhere, so it's ok that there's no offset. It hsould have no skew change or anything.
            warpX += domainWarp.GetNoise(warpX + 20, warpZ + 20) * 2f; //was 5 with pretty incredible results
            warpZ += domainWarp.GetNoise(warpX - 20, warpZ - 20) * 2f;
        }

        return this.getNoise2D((int) warpX, (int) warpZ);
    }

    public boolean isLiquid(int x, int y, int z) {

        if(!enableRiver()) {
            return false;
        }

        BlockPos bPos = new BlockPos(x, y, z);

        if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
            return false;
        }

        if(this.getNoise2D(bPos.getX(), bPos.getZ()) < NOISE_CUTOFF_RIVER_NON_WARPED){
            return false;
        }

        float yLevelNoise = this.getCaveYNoise(x, z);
        int caveY = this.getCaveY(yLevelNoise);
        if(caveY != y) {
            return false;
        }

        float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
        if(noise > NOISE_CUTOFF_RIVER) {
            return true;
        }

        return false;
    }

    public boolean isAir(int x, int y, int z) {

        if(!enableRiver()) {
            return false;
        }

        BlockPos bPos = new BlockPos(x, y, z);

        if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
            return false;
        }


        if(this.getNoise2D(bPos.getX(), bPos.getZ()) < NOISE_CUTOFF_RIVER_NON_WARPED){
            return false;
        }


        float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
        if(noise <= NOISE_CUTOFF_RIVER) {
            return false;
        }

        if (y <= (Globals.minY + 9)) {
            return false;
        }

        if(isLiquid(x, y - 1, z)) {
            return true;
        } else if(isLiquid(x, y - 2, z)) {
            return true;
        }

        float noiseDiff = noise - NOISE_CUTOFF_RIVER;

        //Carve roof
        float noiseDelta = noiseDiff;
        int noiseCutoffCeiling = (int) (noiseDelta * 100);
        noiseCutoffCeiling /= 2;
        int topDelta = noiseCutoffCeiling + 2;
        for(int i = 1; i < noiseCutoffCeiling; i++) {
            //level.setBlockState(curPos.above(2 + i), Blocks.AIR.defaultBlockState(), false);
            if(isLiquid(x, y - (2 + i), z)) {
                return true;
            }
        }

        return false;
    }

    public boolean isNearAir(int x, int y, int z) {

        if(!enableRiver()) {
            return false;
        }

        BlockPos bPos = new BlockPos(x, y, z);

        if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
            return false;
        }


        if(this.getNoise2D(bPos.getX(), bPos.getZ()) < NOISE_CUTOFF_RIVER_NON_WARPED){
            return false;
        }

        float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
        if(noise <= NOISE_CUTOFF_RIVER) {
            return false;
        }

        if (y <= (Globals.minY + 8)) {
            return false;
        }

        return true;
    }




    public boolean isInYRange(int y) {
        //16 is arbitrary, should be more than enough
        //for any cavernous tops + an extra stone as a boundary
        return y >= (this.min_y - 2) && y <= (this.min_y + 16);
    }



    //checkIfInRiver = true for the noise mixin, false = if it's called by waterfall function
    public boolean isBoundary(int x, int y, int z, boolean checkIfInRiver) {

        if(!enableRiver()) {
            return false;
        }

        BlockPos bPos = new BlockPos(x, y, z);

        if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
            return false;
        }

        //was 0.75
        if(this.getNoise2D(bPos.getX(), bPos.getZ()) < NOISE_CUTOFF_RIVER_NON_WARPED){
            return false;
        }

        float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
        boolean shouldCarveRiver = noise > NOISE_CUTOFF_RIVER;
        if(shouldCarveRiver) {
            return false;
        }

        boolean shouldCheckBoundary = true;

        //why do I have this?
        /*
        for(int i = 0; i < 5; i++) {
            if(shouldCheckBoundary) {
                shouldCheckBoundary = NoiseChunkMixinUtils.getRiverLayer(128, x, y - i, z) == null;
            }
        }
         */

        // /tp 4383 -18 3784
        if(shouldCheckBoundary) {
            if(NoiseChunkMixinUtils.getRiverLayer(128, x + 1, y, z) != null) {
                return true;
            } else if(NoiseChunkMixinUtils.getRiverLayer(128, x - 1, y, z) != null) {
                return true;
            } else if(NoiseChunkMixinUtils.getRiverLayer(128, x, y, z + 1) != null) {
                return true;
            } else if(NoiseChunkMixinUtils.getRiverLayer(128, x, y, z - 1) != null) {
                return true;
            }
        }


        /*
        boolean shouldCheckBoundary = true;
        for(int i = 0; i < 5; i++) {
            if(shouldCheckBoundary) {
                shouldCheckBoundary = !NoiseChunkMixinUtils.shouldSetToLava(128, x, y - i, z) &&
                        !NoiseChunkMixinUtils.shouldSetToWater(128, x, y - i, z);
            }
        }

        // /tp 4383 -18 3784
        if(shouldCheckBoundary) {
            if(NoiseChunkMixinUtils.shouldSetToLava(128, x + 1, y, z) || NoiseChunkMixinUtils.shouldSetToWater(128, x + 1, y, z)) {
                return true;
            } else if(NoiseChunkMixinUtils.shouldSetToLava(128, x - 1, y, z) || NoiseChunkMixinUtils.shouldSetToWater(128, x - 1, y, z)) {
                return true;
            } else if(NoiseChunkMixinUtils.shouldSetToLava(128, x, y, z + 1) || NoiseChunkMixinUtils.shouldSetToWater(128, x, y, z + 1)) {
                return true;
            } else if(NoiseChunkMixinUtils.shouldSetToLava(128, x, y, z - 1) || NoiseChunkMixinUtils.shouldSetToWater(128, x, y, z - 1)) {
                return true;
            }
        }

         */

        return false;
    }

    //checkIfInRiver = true for the noise mixin, false = if it's called by waterfall function
    public boolean isBelowWaterfallSupport(int x, int y, int z) {

        if(!enableRiver()) {
            return false;
        }

        BlockPos bPos = new BlockPos(x, y, z);

        if(this.isOutOfBounds(bPos.getX(), bPos.getZ())) {
            return false;
        }

        if(this.getNoise2D(bPos.getX(), bPos.getZ()) < NOISE_CUTOFF_RIVER_NON_WARPED){
            return false;
        }

        float noise = this.getWarpedNoise(bPos.getX(), bPos.getZ());
        boolean noiseAboveCutoff = noise > NOISE_CUTOFF_RIVER;

        float yLevelNoise_o = this.getCaveYNoise(x, z);
        int y_o = this.getCaveY(yLevelNoise_o);

        BlockPos.MutableBlockPos mbPos = new BlockPos.MutableBlockPos();
        mbPos.set(bPos.getX() + 1, bPos.getY(), bPos.getZ());
        if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
            float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
            int neighborY = this.getCaveY(yLevelNoise1);
            return y == neighborY && (y_o != y) && neighborY < y_o;
        }
        mbPos.set(bPos.getX() - 1, bPos.getY(), bPos.getZ());
        if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
            float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
            int neighborY = this.getCaveY(yLevelNoise1);
            return y ==neighborY && y_o != y && neighborY < y_o;
        }
        mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() + 1);
        if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
            float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
            int neighborY = this.getCaveY(yLevelNoise1);
            return y == neighborY && y_o != y && neighborY < y_o;
        }
        mbPos.set(bPos.getX(), bPos.getY(), bPos.getZ() - 1);
        if(this.getWarpedNoise(mbPos.getX(), mbPos.getZ()) > NOISE_CUTOFF_RIVER) {
            float yLevelNoise1 = this.getCaveYNoise(mbPos.getX(), mbPos.getZ());
            int neighborY = this.getCaveY(yLevelNoise1);
            return y == neighborY && y_o != y && neighborY < y_o;
        }

        return false;
    }


    public boolean isBelowRiverSupport(int x, int y, int z) {

        if(!enableRiver()) {
            return false;
        }

        /*
        if(this.getLiquidType() == Blocks.LAVA) {
            if(this.isLava(x, y + 1, z)) {
                return true;
            }
            if(this.isLava(x, y + 2, z)) {
                return true;
            }
        } else {
            if(this.isWater(x, y + 1, z)) {
                return true;
            }
            if(this.isWater(x, y + 2, z)) {
                return true;
            }
        }

         */

        if(this.isLiquid(x, y + 1, z)) {
            return true;
        } else if(this.isLiquid(x, y + 2, z)) {
            return true;
        }

        return false;
    }
}