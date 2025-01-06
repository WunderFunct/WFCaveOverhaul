package wftech.caveoverhaul.carvertypes.rivers;

import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.NotImplementedException;
import wftech.caveoverhaul.utils.Globals;

import java.util.ArrayList;
import java.util.List;

//NUR stands for Noise Underground River
public class NURLayerHolder {

    /*
    Saving incase I need to re-enable
     */
    public static NURLayerHolder INSTANCE =  new NURLayerHolder();

    private List<NURDynamicLayer> riverLayers = new ArrayList<>();

    public boolean shouldSetToAirRivers(int topY, int x, int y, int z) {
        return shouldSetToAirRiversInternal(topY, x, y, z);
    }

    public NURDynamicLayer getRiverLayer(int topY, int x, int y, int z) {
        return getRiverLayerInternal(topY, x, y, z);
    }

    public boolean shouldSetToStone(int topY, int x, int y, int z) {
        return shouldSetToStoneInternal(topY, x, y, z);
    }

    public int[] min_values_water = {
            //water
            -25, -4, -4, -8, 18, 36, 48
    };
    public int[] min_values_lava = {
            //lava
            -56, -56, -42, -25
    };


    public NURLayerHolder() {
        int t_seedOffset = 0;

        for(int min_val: min_values_water) {
            this.addLayer(new NURDynamicLayer(Blocks.WATER, min_val, t_seedOffset));
            t_seedOffset += 5;
        }

        for(int min_val: min_values_lava) {
            this.addLayer(new NURDynamicLayer(Blocks.LAVA, min_val, t_seedOffset));
            t_seedOffset += 5;
        }

        if(Globals.minY < -64) {
            int start_y = -56 - 32;

            while(start_y > (Globals.minY + 8)) {
                this.addLayer(new NURDynamicLayer(Blocks.LAVA, start_y, t_seedOffset));
                t_seedOffset += 5;

                start_y -= 32;
            }
        }
    }


    public void addLayer(NURDynamicLayer layer) {
        this.riverLayers.add(layer);
    }

    public NURDynamicLayer getRiverLayerInternal(int topY, int x, int y, int z) {
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

    public boolean shouldSetToStoneInternal(int topY, int x, int y, int z) {
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