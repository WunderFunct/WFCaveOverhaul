package wftech.caveoverhaul.carvertypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import java.util.Random;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import wftech.caveoverhaul.AirOnlyAquifer;
import wftech.caveoverhaul.CaveOverhaul;
import wftech.caveoverhaul.utils.NoiseChunkMixinUtils;

public class OldWorldCarverv12 extends CaveWorldCarver {

	/*
	 * Can't do literal 1.16.5- caves due to the new heights
	 * With the introduction of deepslate, there's a great chance to rebalance cave densities around
	 * the deepslate introduction layer. It'll create a sense of how deep the player is :)
	 */
	public OldWorldCarverv12(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
    public int getCaveY(Random p_230361_1_, boolean shallow) {
    	if(shallow) {
    		return 130 - p_230361_1_.nextInt(p_230361_1_.nextInt(120) + 1); //130 = average y I'd like the caves to start at
    	} else {
	    	return p_230361_1_.nextInt(p_230361_1_.nextInt(384) + 8);
    	}
    }
    
    public int getCaveYSurface(Random random, ChunkAccess access, ChunkPos cPos) {
    	int xPos = cPos.getBlockX(0);
    	int zPos = cPos.getBlockZ(0);
    	int wgHeight = access.getHeight(Types.WORLD_SURFACE_WG, xPos, zPos);
    	
    	return wgHeight + 2;
    }
    
    @Override
    protected float getThickness(Random p_230359_1_) {
        float lvt_2_1_ = p_230359_1_.nextFloat() * 2.0f + p_230359_1_.nextFloat();
        if (p_230359_1_.nextInt(10) == 0) {
            lvt_2_1_ *= p_230359_1_.nextFloat() * p_230359_1_.nextFloat() * 3.0f + 1.0f;
        }
        return lvt_2_1_;
    }
    
    public void generateRoomCluster(CarvingContext context, 
    		CaveCarverConfiguration config, 
    		ChunkAccess chunk, 
    		Function<BlockPos, Holder<Biome>> posToBiomeMapping, 
    		Random random, 
    		Aquifer aquifer, 
    		ChunkPos chunkPos, 
    		CarvingMask mask, 
    		int minHeight, 
    		int maxHeight, 
    		boolean shallow,
    		boolean surfaceCluster) {

        double x = (double) chunkPos.getBlockX(random.nextInt(16 * 16));
        double y = (double) this.getCaveY(random, shallow) - (shallow ? 0 : 64);
        if(surfaceCluster) {
        	y = this.getCaveYSurface(random, chunk, chunk.getPos());
        }
        double z = (double) chunkPos.getBlockZ(random.nextInt(16 * 16));

        double horizontalRadiusMultiplier = (double) config.horizontalRadiusMultiplier.sample(random);
        double verticalRadiusMultiplier = (double) config.verticalRadiusMultiplier.sample(random);
        double floorLevel = (double) config.floorLevel.sample(random);

        WorldCarver.CarveSkipChecker skipChecker = (world, x1, y1, z1, depth) -> shouldSkip(x1, y1, z1, floorLevel);

        int numRooms = 1;

        if (shallow || random.nextInt(2) == 0) {
            double yScale = (double) config.yScale.sample(random);
            float roomWidth = 1.0F + random.nextFloat() * 6.0F;
            createRoom(context, config, chunk, posToBiomeMapping, aquifer, x, y, z, roomWidth, yScale, mask, skipChecker);
            numRooms += random.nextInt(surfaceCluster ? 1 : 4);
        }

        for (int i = 0; i < numRooms; ++i) {
            float angle = surfaceCluster ? ((float) Math.PI) * 1.5f /*((float) Math.PI) + random.nextFloat() * ((float) Math.PI)*/: random.nextFloat() * ((float) Math.PI * 2F);
            float yOffset = (random.nextFloat() - 0.5F) / 2.0F;
            float tunnelWidth = getThickness(random) + 4.0f; // adjusted to be above 1
            int endHeight = minHeight - random.nextInt(minHeight / 4);
            
            
            this.addTunnel12(
            	    context,
            	    config,
            	    posToBiomeMapping,
            	    random.nextLong(),
            	    aquifer,
            	    chunk, 
            	    x, 
            	    y, 
            	    z, 
            	    tunnelWidth, 
            	    angle,
            	    yOffset, 
            	    0, 
            	    endHeight, 
            	    this.getYScale(),
            	    mask,
            	    surfaceCluster);
        }
    }
    
    protected boolean shouldCarve(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, Random random, ChunkPos chunkPos) {
    	return true;
    }

	@Override
	public boolean carve(
		   CarvingContext ctx, 
		   CaveCarverConfiguration cfg, 
		   ChunkAccess level, 
		   Function<BlockPos, Holder<Biome>> pos2BiomeMapping, 
		   Random random, 
		   Aquifer disabled, 
		   ChunkPos chunkPos, 
		   CarvingMask mask) {
		
		if(!this.shouldCarve(ctx, cfg, level, random, chunkPos)) {
			return true;
		}

		int minHeight = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
		int maxHeight = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(2, 8); // was +1 at the end
		Aquifer airAquifer = new AirOnlyAquifer(level, random.nextFloat() <=  0.15f);
	
		for(int k = 0; k < maxHeight; ++k) {
			this.generateRoomCluster(
				ctx, cfg, level, pos2BiomeMapping, random, 
				airAquifer, chunkPos, mask, minHeight, maxHeight, false, false);
		}
		
		maxHeight = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(2); // was +1 at the end
		for(int k = 0; k < maxHeight; ++k) {
			this.generateRoomCluster(
				ctx, cfg, level, pos2BiomeMapping, random, 
				airAquifer, chunkPos, mask, minHeight, maxHeight, true, false);
		}
		
		if(random.nextFloat() <= 0.5) {
			this.generateRoomCluster(
				ctx, cfg, level, pos2BiomeMapping, random, 
				airAquifer, chunkPos, mask, minHeight, maxHeight, true, true);
		}
	
		return true;
	}

	@Override
	public int getRange() {
		return 8;
	}

	protected void addTunnel12(
		    CarvingContext context,
		    CaveCarverConfiguration configuration,
		    Function<BlockPos, Holder<Biome>> biomeFunction,
		    long seed,
		    Aquifer _aquifer,
		    ChunkAccess chunkPrimer, 
		    double initialX, 
		    double initialY, 
		    double initialZ, 
		    float yaw, 
		    float pitch,
		    float unkModifier, 
		    int curNode, 
		    int endNode, 
		    double length,
		    CarvingMask carvingMask,
		    boolean surface) {
		
	    boolean initialFlag;
	    
	    Random random = new Random(seed);
	    Aquifer aquifer = new AirOnlyAquifer(chunkPrimer, surface ? true : random.nextFloat() <=  0.15f);
	    
	    int skipEvery = 0;
	    MutableBlockPos mbPosCheckAir = new BlockPos.MutableBlockPos();
	    List<BlockPos> airPosList = new ArrayList<>();

	    double startX = chunkPrimer.getPos().getMiddleBlockX();
	    double startZ = chunkPrimer.getPos().getMiddleBlockZ();
	    int minBlockX = chunkPrimer.getPos().getMinBlockX();
	    int minBlockZ = chunkPrimer.getPos().getMinBlockZ();
	    
	    float pitchChange = 0.0f;
	    float pitchChangeRate = 0.0f;
	
	    if (endNode <= 0) {
	        int maxendNode = this.getRange() * 16 - 16; //was this.range
	        endNode =  maxendNode - (random.nextInt(maxendNode / 4));
	    }
	
	    boolean flag2 = false;
	
	    if (curNode == -1) {
	        curNode = endNode / 2;
	        flag2 = true;
	    }
	
	    int j = random.nextInt(endNode / 2) + endNode / 4;
	    boolean isRandomFlag = random.nextInt(6) == 0; //CHANGED was nextInt(6)
	
	    while (curNode < endNode) {
	    	//1.5 for the first term by default
	        double yStepApprox = 2.5 + (double)(Mth.sin((float)curNode * 3.1415927f / (float)endNode) * unkModifier);
	        double yStep = yStepApprox * length;
	        float yawChangeRate = Mth.cos(pitch);
	        float pitchChangeRateY = Mth.sin(pitch);
	        initialX += (double)(Mth.cos(yaw) * yawChangeRate);
	        initialY += (double)pitchChangeRateY;
	        initialZ += (double)(Mth.sin(yaw) * yawChangeRate);
	        pitch = surface ? 
	        		(isRandomFlag ? (pitch *= 0.7f) : (pitch *= 0.92f)) : 
        			(isRandomFlag ? (pitch *= 0.92f) : (pitch *= 0.7f)); //CHANGED was 0.7
	        pitch += pitchChangeRate * 0.1f;
	        yaw += pitchChange * 0.1f;
	        pitchChangeRate *= 0.9f;
	        pitchChange *= 0.75f;
	        pitchChangeRate += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
	        pitchChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
	
	        if (!flag2 && curNode == j && unkModifier > 1.0f && endNode > 0) {
	            this.addTunnel12(
	            		context, configuration, biomeFunction, random.nextLong(), aquifer, chunkPrimer, initialX, initialY, initialZ, random.nextFloat() * 0.5f + 0.5f, pitch - 1.5707964f, unkModifier / 3.0f, curNode, endNode, 1.0, carvingMask, false);
	            this.addTunnel12(
	            		context, configuration, biomeFunction, random.nextLong(), aquifer, chunkPrimer, initialX, initialY, initialZ, random.nextFloat() * 0.5f + 0.5f, pitch + 1.5707964f, unkModifier / 3.0f, curNode, endNode, 1.0, carvingMask, false);
	            return;
	        }
	
	        if (flag2|| random.nextInt(4) != 0) {
	            double deltaX = initialX - startX;
	            double deltaZ = initialZ - startZ;
	            double nodesRemaining = endNode - curNode;
	            double maxNodeLength = unkModifier + 2.0f + 16.0f;
	            
	            if(surface) {
	            	//CaveOverhaul.LOGGER.error("[CaveOverhaul] OldWorldCarver 1 -> " + deltaX + ", " + deltaZ + ", " + nodesRemaining + ", " + maxNodeLength);
	            	//CaveOverhaul.LOGGER.error("[CaveOverhaul] OldWorldCarver 2 -> " + curNode + ", " + endNode + ", " + pitch + ", " + pitchChangeRateY);
	            	//CaveOverhaul.LOGGER.error("[CaveOverhaul] OldWorldCarver 3 -> " + ( Mth.floor(initialY - yStep) - 1) + " to " + Mth.floor(initialY + yStep) + 1);
	            }
	
	            if (deltaX * deltaX + deltaZ * deltaZ - nodesRemaining * nodesRemaining > maxNodeLength * maxNodeLength) {
	                return;
	            }
	            
	            mbPosCheckAir.set(initialX, initialY, initialZ);
	            boolean flagInAir = chunkPrimer.getBlockState(mbPosCheckAir).isAir();
	            if(flagInAir) {
	            	airPosList.add(new BlockPos((int) initialX, (int) initialY, (int) initialZ));
	            }
	
	            if (initialX >= startX - 16.0 - yStepApprox * 2.0 && initialZ >= startZ - 16.0 - yStepApprox * 2.0 && initialX <= startX + 16.0 + yStepApprox * 2.0 && initialZ <= startZ + 16.0 + yStepApprox * 2.0) {
	                int minX = Mth.floor(initialX - yStepApprox) - minBlockX - 1;
	                int maxX = Mth.floor(initialX + yStepApprox) - minBlockX + 1;
	                
	                int minY = Mth.floor(initialY - yStep) - 1;
	                int maxY = Mth.floor(initialY + yStep) + 1;
	                
	                int minZ = Mth.floor(initialZ - yStepApprox) - minBlockZ - 1;
	                int maxZ = Mth.floor(initialZ + yStepApprox) - minBlockZ + 1;
	
	                if (minX < 0) {
	                    minX = 0;
	                }
	
	                if (maxX > 16) {
	                    maxX = 16;
	                }
	
	                if (minY < -63) {
	                    minY = -63;
	                }
	
	                // CHANGED: was 248
	                if (maxY > 120) {
	                    maxY = 120;
	                }
	
	                if (minZ < 0) {
	                    minZ = 0;
	                }
	
	                if (maxZ > 16) {
	                    maxZ = 16;
	                }
	
	                boolean isInOcean = false;
	                
	                ChunkPos chunkPos = chunkPrimer.getPos();
	
	                if (!isInOcean) {
	                    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
	        	        BlockPos.MutableBlockPos mutableBlockPos1 = new BlockPos.MutableBlockPos();
	
	                    for (int xIter = minX; xIter < maxX; ++xIter) {
	                        double xTargetSize = ((double)(xIter + minBlockX) + 0.5 - initialX) / yStepApprox;
	        	            int blockX = chunkPos.getBlockX(xIter);
	
	                        for (int zIter = minZ; zIter < maxZ; ++zIter) {
	            	            int blockZ = chunkPos.getBlockZ(zIter);
	                            double zTargetSize = ((double)(zIter + minBlockZ) + 0.5 - initialZ) / yStepApprox;
	                            boolean isTopBlock = false;
	                            
	
	                            //CHANGED: first skip, originally set to skip @ 1.0
	                            if (xTargetSize * xTargetSize + zTargetSize * zTargetSize >= 4.0) continue;
	                            
	        	                MutableBoolean shouldCarve = new MutableBoolean(false);
	
	                            for (int yIter = maxY; yIter > minY; --yIter) {
	                                double yTargetSize = ((double)(yIter - 1) + 0.5 - initialY) / yStep;
	        	                    
	        	                    if(NoiseChunkMixinUtils.shouldSetToLava(128, blockX, yIter, blockZ)) {
	        	                        continue;
	        	            		} else if(NoiseChunkMixinUtils.shouldSetToWater(128, blockX, yIter, blockZ)) {
	        	                        continue;
	        	            		} else if(NoiseChunkMixinUtils.shouldSetToStone(128, blockX, yIter, blockZ)) {
	        	                        continue;
	        	            		} else if(NoiseChunkMixinUtils.shouldSetToLava(128, blockX, yIter + 1, blockZ)) {
	        	                        continue;
	        	            		} else if(NoiseChunkMixinUtils.shouldSetToWater(128, blockX, yIter + 1, blockZ)) {
	        	                        continue;
	        	            		}

	                                if (yTargetSize <= -0.7 || 
	                                		xTargetSize * xTargetSize + yTargetSize * yTargetSize + zTargetSize * zTargetSize >= 1.0) continue;

	                                mutableBlockPos.set(blockX, yIter, blockZ);
	                                
	                                this.carveBlock(context, configuration, chunkPrimer, biomeFunction, carvingMask, mutableBlockPos,
	            	                        mutableBlockPos1, aquifer, shouldCarve);
	                            }
	                        }
	                    }
	
	                    if (flag2) break;
	                }
	            }
	            
	        ++curNode;
	        }
	    }
	    
	}
}
