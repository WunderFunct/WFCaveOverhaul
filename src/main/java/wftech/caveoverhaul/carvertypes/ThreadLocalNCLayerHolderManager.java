package wftech.caveoverhaul.carvertypes;

import net.minecraftforge.server.ServerLifecycleHooks;

import wftech.caveoverhaul.utils.Globals;

/*
Originally created for enabling caching; overhead nullified most of the benefit of this method
Keeping it around for one patch so I can reference it if need be later on
 */
public class ThreadLocalNCLayerHolderManager {
    private static final ThreadLocal<NCLayerHolder> THREAD_LOCAL_HOLDER = ThreadLocal.withInitial(() -> {
        int minY = Globals.minY; // Retrieve minY dynamically
        //int seed = (int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed();
        return new NCLayerHolder(minY);
    });

    public static NCLayerHolder getCurrentHolder() {
        return THREAD_LOCAL_HOLDER.get();
    }

    public static void clear() {
        THREAD_LOCAL_HOLDER.remove(); // Cleanup if needed
    }
}