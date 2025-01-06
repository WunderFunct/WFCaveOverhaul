package wftech.caveoverhaul.carvertypes;

import net.minecraftforge.server.ServerLifecycleHooks;
import wftech.caveoverhaul.fastnoise.FastNoiseLite;
import wftech.caveoverhaul.utils.FloatPos;

public class NoisetypeDomainWarp {

    /*
    Static
     */

    private static NoisetypeDomainWarp INSTANCE = null;

    public static void init(int minY){
        INSTANCE = new NoisetypeDomainWarp(minY);
    }

    public static FloatPos getWarpedPosition(float xPos, float yPos, float zPos) {
        return INSTANCE.getWarpedPositionInternal(xPos, yPos, zPos);
    }

    /*
    Actual class
     */

    private FastNoiseLite domainWarp = null;
    private int minY = -64;

    public NoisetypeDomainWarp(int minY) {
        this.minY = Math.abs(minY);
    }

    public void initDomainWarp() {

        if(domainWarp != null) {
            return;
        }

        FastNoiseLite tnoise = new FastNoiseLite();
        tnoise.SetSeed((int) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions().seed());
        tnoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        tnoise.SetFrequency(0.01f);
        domainWarp = tnoise;
    }

    /* **CHANGED**
     * v2 change:
     * 1. disable yPos>=0 -> return no warp
     * 2. make warpslide slide from 64 to -64
     * 3. Make y > 0 iterate 1 time
     * 4. Make the warp coord offsets dependent on y pos, slide between 5 and 30?
     * 		64 -> 0: 5 -> 10
     * 		0 -> -64: 10 -> 30
     */
    public FloatPos getWarpedPositionInternal(float xPos, float yPos, float zPos) {

        //CHANGED, was true
        //v2 change
		/*
		if(yPos >= 0)
		{
			return getCaveDetailsNoise(xPos, yPos, zPos);
		}
		*/

        if(domainWarp == null) {
            initDomainWarp();
        }

        //v2
        //Originally was yPos + 64;
        // then 25f * ( tYPos / 128f )
        float tYPos = yPos + minY;
        float warpSlide = 25f * ( tYPos / (minY * 2f) );

        //float yOrig = yPos / 2f;
        //float yAdjPart = ( -yPos / 64f);
        //float yAdj = 2f - yAdjPart;

        float warpX = xPos;
        float warpY = yPos;
        float warpZ = zPos;

        //v2 change
        int iterAmounts = yPos >= 0 ? 2 : 3;
        float yPosClamped = yPos > 64 ? 64f : (float) yPos;
        float warpOffsetF = yPos >= 0 ? ((yPosClamped / 64f) * 5f) + 5f : (((-yPosClamped) / 64f) * 20f) + 10f;
        int warpOffset = Math.round(warpOffsetF);

        for(int i = 0; i < iterAmounts; i++) {
            //CHANGED
            //Not applying an offset to warpX is intentional.
            //The location for warpX can be anywhere, so it's ok that there's no offset. It hsould have no skew change or anything.
            warpY += domainWarp.GetNoise(warpX, warpY, warpZ) * warpSlide; //was 5 with pretty incredible results
            warpX += domainWarp.GetNoise(warpX + warpOffset, warpY + warpOffset, warpZ + warpOffset) * warpSlide;
            warpZ += domainWarp.GetNoise(warpX - warpOffset, warpY - warpOffset, warpZ - warpOffset) * warpSlide;
        }

        FloatPos fPos = new FloatPos(warpX, warpY, warpZ);

        return fPos;
        //return getCaveDetailsNoise(warpX, warpY, warpZ);
    }
}
