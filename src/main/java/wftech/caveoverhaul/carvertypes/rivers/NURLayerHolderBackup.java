package wftech.caveoverhaul.carvertypes.rivers;

import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

//NUR stands for Noise Underground River
public class NURLayerHolderBackup {

    private List<NURDynamicLayer> riverLayers = new ArrayList<>();
    private boolean initialized = false;

    public static NURLayerHolderBackup INSTANCE = new NURLayerHolderBackup();

    public static void init(int global_min_y){

        if(INSTANCE.initialized) {
            return;
        }

        /*
        For pre-existing layers
         */

        //lava
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.LAVA, -56, 53));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.LAVA, -56, 56));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.LAVA, -42, 59));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.LAVA, -25, 62));

        //water
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, -25, 65));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, -4, 68));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, -4, 71));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, -8, 73));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, 18, 76));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, 36, 79));
        INSTANCE.addLayer(new NURDynamicLayer(Blocks.WATER, 48, 82));

        /*
        Dynamic sub-64 layers
         */

        //CaveOverhaul.LOGGER.debug("[CaveOverhaul] Non-standard overworld y level detected: " + global_min_y);
        if (global_min_y != -64) {

            int curOffset = 82 + 3;

            for(int i = -56; i > global_min_y; i -= 16) {
                INSTANCE.addLayer(new NURDynamicLayer(Blocks.LAVA, i, curOffset));
                curOffset += 3;
            }

        }

        INSTANCE.setInitialized();
    }

    /*
    public static boolean shouldSetToWater2(int topY, int x, int y, int z) {
        return INSTANCE.shouldSetToWaterInternal(topY, x, y, z);
    }

    public static boolean shouldSetToLava2(int topY, int x, int y, int z) {
        return INSTANCE.shouldSetToLavaInternal(topY, x, y, z);
    }

     */

    public static boolean shouldSetToAirRivers(int topY, int x, int y, int z) {
        return INSTANCE.shouldSetToAirRiversInternal(topY, x, y, z);
    }

    public static NURDynamicLayer getRiverLayer(int topY, int x, int y, int z) {
        return INSTANCE.getRiverLayerInternal(topY, x, y, z);
    }

    public static boolean shouldSetToStone(int topY, int x, int y, int z) {
        return INSTANCE.shouldSetToStoneInternal(topY, x, y, z);
    }

    public NURLayerHolderBackup() {}

    public void setInitialized(){
        this.initialized = true;
    }

    public void throwIfNotInitialized() {
        if (!this.initialized) {
            throw new NotImplementedException("Coding error for Cave Overhaul. Attempted to run noise checks without initializing NURDynamicHolder. Please inform W.F. of this error, preferably via commenting on the Curseforge mod page.");
        }
    }

    public void addLayer(NURDynamicLayer layer) {
        this.riverLayers.add(layer);
    }

    public NURDynamicLayer getRiverLayerInternal(int topY, int x, int y, int z) {
        this.throwIfNotInitialized();

        for (NURDynamicLayer layer: this.riverLayers) {

            if(!layer.isInYRange(y)) {
                continue;
            }

            if(layer.isLiquid(x, y, z)) {
                return layer;
            }
        }

        return null;
    }

    /*
    public boolean shouldSetToWaterInternal2(int topY, int x, int y, int z) {
        this.throwIfNotInitialized();

        for (NURDynamicLayer layer: this.riverLayers) {
            if(layer.isWater(x, y, z)) {
                return true;
            }
        }

        return false;
    }

    public boolean shouldSetToLavaInternal2(int topY, int x, int y, int z) {
        this.throwIfNotInitialized();

        for (NURDynamicLayer layer: this.riverLayers) {
            if(layer.isLava(x, y, z)) {
                return true;
            }
        }

        return false;
    }

     */

    public boolean shouldSetToStoneInternal(int topY, int x, int y, int z) {
        this.throwIfNotInitialized();

        for (NURDynamicLayer layer: this.riverLayers) {

            if(!layer.isInYRange(y)) {
                continue;
            }

            if(layer.isBelowRiverSupport(x, y, z)) {
                return true;
            }
            if(layer.isBelowWaterfallSupport(x, y, z)) {
                return true;
            }
            if(layer.isBoundary(x, y, z, true)) {
                return true;
            }
        }

        return false;
    }

    public boolean shouldSetToAirRiversInternal(int topY, int x, int y, int z) {
        this.throwIfNotInitialized();

        for (NURDynamicLayer layer: this.riverLayers) {

            if(!layer.isInYRange(y)) {
                continue;
            }

            if(layer.isAir(x, y, z)) {
                return true;
            }
        }

        return false;
    }

}