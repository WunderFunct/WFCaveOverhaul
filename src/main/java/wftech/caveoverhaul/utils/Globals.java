package wftech.caveoverhaul.utils;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class Globals {

    public static int minY = 0;
    public static boolean volcanicCavernsCheckComplete = false;
    public static boolean isVolcanicCavernsLoaded = false;

    public static void init(){

        if (volcanicCavernsCheckComplete) {
            return;
        }

        for(IModInfo modInfo: ModList.get().getMods()) {
            if((!isVolcanicCavernsLoaded) && modInfo.getNamespace().contains("volcanic_caverns")) {
                isVolcanicCavernsLoaded = true;
                break;
            }
        }

        volcanicCavernsCheckComplete = true;
    }
}