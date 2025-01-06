package wftech.caveoverhaul.carvertypes.rivers;

import wftech.caveoverhaul.carvertypes.NCLayerHolder;
import wftech.caveoverhaul.utils.Globals;

/*
something something worked in a potato factory, graduated from culinary academy...

For those who got it, too much rooftime makes a bad modmaker sad

Originally created for enabling caching; overhead nullified most of the benefit of this method
Keeping it around for one patch so I can reference it if need be later on
 */
public class ThreadLocalNURLayerHolderManager {
    private static final ThreadLocal<NURLayerHolder> THREAD_LOCAL_HOLDER = ThreadLocal.withInitial(NURLayerHolder::new);

    public static NURLayerHolder getCurrentHolder() {
        return THREAD_LOCAL_HOLDER.get();
    }

    public static void clear() {
        THREAD_LOCAL_HOLDER.remove(); // Cleanup if needed
    }

}
