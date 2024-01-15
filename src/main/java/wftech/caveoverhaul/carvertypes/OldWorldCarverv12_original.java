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
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import wftech.caveoverhaul.AirOnlyAquifer;
import wftech.caveoverhaul.CaveOverhaul;

public class OldWorldCarverv12_original extends CaveWorldCarver {

	/*
	 * Can't do literal 1.16.5- caves due to the new heights
	 * With the introduction of deepslate, there's a great chance to rebalance cave densities around
	 * the deepslate introduction layer. It'll create a sense of how deep the player is :)
	 */
	public OldWorldCarverv12_original(Codec<CaveCarverConfiguration> p_159194_) {
		super(p_159194_);
	}
	
    public int getCaveY(Random p_230361_1_, boolean shallow) {
    	if(shallow) {
    		return 130 - p_230361_1_.nextInt(p_230361_1_.nextInt(120) + 1); //130 = average y I'd like the caves to start at
    	} else {
	    	return p_230361_1_.nextInt(p_230361_1_.nextInt(384) + 8);
    	}
    }
    
    @Override
    protected float getThickness(Random p_230359_1_) {
        float lvt_2_1_ = p_230359_1_.nextFloat() * 2.0f + p_230359_1_.nextFloat();
        if (p_230359_1_.nextInt(10) == 0) {
            lvt_2_1_ *= p_230359_1_.nextFloat() * p_230359_1_.nextFloat() * 3.0f + 1.0f;
        }
        return lvt_2_1_;
    }
    
    public void generateVerticalCluster(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, Function<BlockPos, Holder<Biome>> pos2BiomeMapping, Random random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask mask, int i, int j, boolean shallow) {

        double d0 = (double)chunkPos.getBlockX(random.nextInt(16 * 16));
        double d1 = (double) this.getCaveY(random, shallow) - (shallow ? 0 : 64);
        double d2 = (double)chunkPos.getBlockZ(random.nextInt(16 * 16));
        
        double d3 = (double)cfg.horizontalRadiusMultiplier.sample(random);
        double d4 = (double)cfg.verticalRadiusMultiplier.sample(random);
        double d5 = (double)cfg.floorLevel.sample(random);
        
        WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) -> {
           return shouldSkip(p_159203_, p_159204_, p_159205_, d5);
        };
        
        int l = 1;

        if (shallow || random.nextInt(2) == 0) {
	            double d6 = (double)cfg.yScale.sample(random);
	            float f1 = 1.0F + random.nextFloat() * 6.0F;
	            this.createRoom(ctx, cfg, level, pos2BiomeMapping, aquifer, d0, d1, d2, f1, d6, mask, worldcarver$carveskipchecker);
	            l += random.nextInt(4);
	         }

        for(int k1 = 0; k1 < l; ++k1) {
            //float angle = random.nextFloat() * ((float)Math.PI * 2F);
            float angle = random.nextFloat() * ((float)Math.PI * 2F);
           float f3 = (random.nextFloat() - 0.5F) / 2.0F;; //(random.nextFloat() - 0.5F) / 2.0F;
           float f2 = this.getThickness(random); //added the +1f
           int i1 = i - random.nextInt(i / 4);
           int j1 = 0;
           this.createTunnel(ctx, 
        		   cfg, 
        		   level, 
        		   pos2BiomeMapping, 
        		   random.nextLong(), 
        		   aquifer, 
        		   d0, 
        		   d1, 
        		   d2, 
        		   d3, //d3
        		   d4, //d4
        		   0f, //f2
        		   0f, //angle
        		   0f, //f3
        		   0, 
        		   i1, //i
        		   this.getYScale(), 
        		   mask, 
        		   worldcarver$carveskipchecker);
        }
    }
    	
    /*
    public void generateRoomCluster(CarvingContext ctx, CaveCarverConfiguration cfg, ChunkAccess level, Function<BlockPos, Holder<Biome>> pos2BiomeMapping, Random random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask mask, int i, int j, boolean shallow) {

        double d0 = (double)chunkPos.getBlockX(random.nextInt(16 * 16));
        double d1 = (double) this.getCaveY(random, shallow) - (shallow ? 0 : 64);
        double d2 = (double)chunkPos.getBlockZ(random.nextInt(16 * 16));
        
        double d3 = (double)cfg.horizontalRadiusMultiplier.sample(random);
        double d4 = (double)cfg.verticalRadiusMultiplier.sample(random);
        double d5 = (double)cfg.floorLevel.sample(random);
        
        WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) -> {
           return shouldSkip(p_159203_, p_159204_, p_159205_, d5);
        };
        
        int l = 1;

        if (shallow || random.nextInt(2) == 0) {
	            double d6 = (double)cfg.yScale.sample(random);
	            float f1 = 1.0F + random.nextFloat() * 6.0F;
	            this.createRoom(ctx, cfg, level, pos2BiomeMapping, aquifer, d0, d1, d2, f1, d6, mask, worldcarver$carveskipchecker);
	            l += random.nextInt(4);
	         }

        for(int k1 = 0; k1 < l; ++k1) {
           float angle = random.nextFloat() * ((float)Math.PI * 2F);
           float f3 = (random.nextFloat() - 0.5F) / 2.0F;
           float f2 = this.getThickness(random); //added the +1f
           int endHeight = i - random.nextInt(i / 4);
           int j1 = 0;
           this.createTunnel(ctx, 
        		   cfg, 
        		   level, 
        		   pos2BiomeMapping, 
        		   random.nextLong(), 
        		   aquifer, 
        		   d0, 
        		   d1, 
        		   d2, 
        		   d3, 
        		   d4, 
        		   f2, //should be above 1 to spawn more tunnels
        		   angle, 
        		   f3, 
        		   0, 
        		   endHeight, //i
        		   this.getYScale(), 
        		   mask, 
        		   worldcarver$carveskipchecker);
        }
    	
    }
    */
    
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
    		boolean shallow) {

        double x = (double) chunkPos.getBlockX(random.nextInt(16 * 16));
        double y = (double) this.getCaveY(random, shallow) - (shallow ? 0 : 64);
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
            numRooms += random.nextInt(4);
        }

        for (int i = 0; i < numRooms; ++i) {
            float angle = random.nextFloat() * ((float) Math.PI * 2F);
            float yOffset = (random.nextFloat() - 0.5F) / 2.0F;
            float tunnelWidth = getThickness(random) + 4.0f; // adjusted to be above 1
            int endHeight = minHeight - random.nextInt(minHeight / 4);
            
            /*
            this.createTunnel(
            		context, 
            		config, 
            		chunk, 
            		posToBiomeMapping, 
            		random.nextLong(), 
            		aquifer, 
            		x, 
            		y, 
            		z, 
            		horizontalRadiusMultiplier, 
            		verticalRadiusMultiplier, 
            		tunnelWidth, 
            		angle, 
            		yOffset, 
            		0, 
            		endHeight, 
            		this.getYScale(), 
            		mask, 
            		skipChecker);
            */
            
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
            	    mask);
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
		   Aquifer aquifer, 
		   ChunkPos chunkPos, 
		   CarvingMask mask) {
		
		if(!this.shouldCarve(ctx, cfg, level, random, chunkPos)) {
			return true;
		}

		int minHeight = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
		int maxHeight = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(2, 8); // was +1 at the end
		Aquifer airAquifer = new AirOnlyAquifer(level, random.nextFloat() <= 0.3f);
	
		for(int k = 0; k < maxHeight; ++k) {
			//generateRoomCluster
			//generateVerticalCluster
			this.generateRoomCluster(
				ctx, cfg, level, pos2BiomeMapping, random, 
				airAquifer, chunkPos, mask, minHeight, maxHeight, false);
		}
		
		maxHeight = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(2); // was +1 at the end
		for(int k = 0; k < maxHeight; ++k) {
			this.generateRoomCluster(
				ctx, cfg, level, pos2BiomeMapping, random, 
				airAquifer, chunkPos, mask, minHeight, maxHeight, true);
		}
	
		/*
		j = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1) + random.nextInt(1, 3); // was +1 at the end
		for(int k = 0; k < j; ++k) {
		this.generateRoomCluster(ctx, cfg, level, pos2BiomeMapping, random, airAquifer, chunkPos, mask, i, j, true); //moar shalow clusterszzz
		}
		*/
	
		return true;
	}
	
	//with help from ChatGPT :)
	@Override
	protected void createTunnel(
		    CarvingContext context,
		    CaveCarverConfiguration configuration,
		    ChunkAccess chunkAccess,
		    Function<BlockPos, Holder<Biome>> biomeFunction,
		    long randomSeed,
		    Aquifer aquifer,
		    double startX,
		    double startY,
		    double startZ,
		    double horizontalRadius,
		    double verticalRadius,
		    float thickness,
		    float angleYaw,
		    float anglePitch,
		    int startHeight,
		    int endHeight,
		    double heightScale,
		    CarvingMask carvingMask,
		    WorldCarver.CarveSkipChecker skipChecker
		) {
		    Random random = new Random(randomSeed);
		    int segmentCount = random.nextInt(endHeight / 2) + endHeight / 4;
		    segmentCount += 5;
		    boolean widen = random.nextInt(6) == 0;
		    float anglePitchChange = 0.0f;
		    float angleYawChange = 0.0f;

		    for (int height = startHeight; height < endHeight; ++height) {
		        double radius = 1.5 + (double)(Mth.sin((float)Math.PI * (float)height / (float)endHeight) * thickness);
		        double scaledVerticalRadius = radius * heightScale;
		        float cosPitch = Mth.cos(anglePitch);
		        
		        startX += (double)(Mth.cos(angleYaw) * cosPitch);
		        startY += (double)Mth.sin(anglePitch);
		        startZ += (double)(Mth.sin(angleYaw) * cosPitch);

		        angleYaw *= widen ? 0.92f : 0.7f;
		        angleYaw += angleYawChange * 0.1f;
		        anglePitch += anglePitchChange * 0.1f;
		        anglePitchChange *= 0.9f;
		        angleYawChange *= 0.75f;
		        anglePitchChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
		        angleYawChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;

		        if (height == segmentCount && thickness > 1.0f) {
		            // Create branching tunnels
		            this.createTunnel(context, configuration, chunkAccess, biomeFunction, random.nextLong(), aquifer, startX, startY, startZ,
		                horizontalRadius, verticalRadius, random.nextFloat() * 0.5f + 0.5f, anglePitch - 1.5707964f, angleYaw / 3.0f,
		                height, endHeight, 1.0, carvingMask, skipChecker);
		            this.createTunnel(context, configuration, chunkAccess, biomeFunction, random.nextLong(), aquifer, startX, startY, startZ,
		                horizontalRadius, verticalRadius, random.nextFloat() * 0.5f + 0.5f, anglePitch + 1.5707964f, angleYaw / 3.0f,
		                height, endHeight, 1.0, carvingMask, skipChecker);
		            return;
		        }

		        if (random.nextInt(4) == 0) continue;

		        if (!CaveWorldCarver.canReach(chunkAccess.getPos(), startX, startZ, height, endHeight, thickness)) {
		            return;
		        }

		        //INCREASED BY 1d , increase by more?
		        //Be sure to disable old caves for checking purposes :)
		        this.carveEllipsoidF(context, configuration, chunkAccess, biomeFunction, aquifer, startX, startY, startZ,
			            (radius * horizontalRadius) + 2d, (scaledVerticalRadius) + 2d, carvingMask, skipChecker);
		    }
		}



	protected boolean carveEllipsoidF(
	    CarvingContext context,
	    CaveCarverConfiguration configuration,
	    ChunkAccess chunkAccess,
	    Function<BlockPos, Holder<Biome>> biomeFunction,
	    Aquifer aquifer,
	    double centerX,
	    double centerY,
	    double centerZ,
	    double horizontalRadius,
	    double verticalRadius,
	    CarvingMask carvingMask,
	    CarveSkipChecker skipChecker
	) {
	    ChunkPos chunkPosition = chunkAccess.getPos();
	    double chunkMiddleX = chunkPosition.getMiddleBlockX();
	    double chunkMiddleZ = chunkPosition.getMiddleBlockZ();
	    double maxOffset = 16.0 + horizontalRadius * 2.0;
	
	    if (!(Math.abs(centerX - chunkMiddleX) > maxOffset) && !(Math.abs(centerZ - chunkMiddleZ) > maxOffset)) {
	        int minBlockX = chunkPosition.getMinBlockX();
	        int minBlockZ = chunkPosition.getMinBlockZ();
	        
	        int minX = Math.max(Mth.floor(centerX - horizontalRadius) - minBlockX - 1, 0);
	        int maxX = Math.min(Mth.floor(centerX + horizontalRadius) - minBlockX, 15);
	        
	        int minY = Math.max(Mth.floor(centerY - verticalRadius) - 1, context.getMinGenY() + 1);
	        int maxY = chunkAccess.isUpgrading() ? 0 : 7;
	        
	        int maxZ = Math.min(Mth.floor(centerY + verticalRadius) + 1, context.getMinGenY() + context.getGenDepth() - 1 - maxY);
	        int minZ = Math.max(Mth.floor(centerZ - horizontalRadius) - minBlockZ - 1, 0);
	        
	        int maxZ2 = Math.min(Mth.floor(centerZ + horizontalRadius) - minBlockZ, 15);
	
	        boolean carved = false;
	        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
	        BlockPos.MutableBlockPos mutableBlockPos1 = new BlockPos.MutableBlockPos();
	
	        for (int x = minX; x <= maxX; ++x) {
	            int blockX = chunkPosition.getBlockX(x);
	            double offsetX = ((double) blockX + 0.5 - centerX) / horizontalRadius;
	
	            for (int z = minZ; z <= maxZ2; ++z) {
	                int blockZ = chunkPosition.getBlockZ(z);
	                double offsetZ = ((double) blockZ + 0.5 - centerZ) / horizontalRadius;
	
	                if (offsetX * offsetX + offsetZ * offsetZ >= 1.0)
	                    continue;
	
	                MutableBoolean shouldCarve = new MutableBoolean(false);
	
	                for (int y = maxY; y > minY; --y) {
	                    double offsetY = ((double) y - 0.5 - centerY) / verticalRadius;
	
	                    if (skipChecker.shouldSkip(context, offsetX, offsetY, offsetZ, y) ||
	                        carvingMask.get(x, y, z))
	                        continue;
	
	                    carvingMask.set(x, y, z);
	                    mutableBlockPos.set(blockX, y, blockZ);
	
	                    carved |= this.carveBlock(context, configuration, chunkAccess, biomeFunction, carvingMask, mutableBlockPos,
	                        mutableBlockPos1, aquifer, shouldCarve);
	                }
	            }
	        }
	
	        return carved;
	    }
	
	    return false;
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
		    Aquifer aquifer,
		    ChunkAccess chunkPrimer, 
		    double initialX, 
		    double initialY, 
		    double initialZ, 
		    float yaw, 
		    float pitch,
		    float pitchModifier, 
		    int startDiameter, 
		    int endDiameter, 
		    double length,
		    CarvingMask carvingMask) {
		
	    boolean initialFlag;
	    
	    int skipEvery = 0;
	    MutableBlockPos mbPosCheckAir = new BlockPos.MutableBlockPos();
	    List<BlockPos> airPosList = new ArrayList<>();

	    double startX = chunkPrimer.getPos().getMiddleBlockX();
	    double startZ = chunkPrimer.getPos().getMiddleBlockZ();
	    int minBlockX = chunkPrimer.getPos().getMinBlockX();
	    int minBlockZ = chunkPrimer.getPos().getMinBlockZ();
	    
	    float pitchChange = 0.0f;
	    float pitchChangeRate = 0.0f;
	    Random random = new Random(seed);
	
	    if (endDiameter <= 0) {
	        int maxEndDiameter = this.getRange() * 16 - 16; //was this.range
	        endDiameter = maxEndDiameter - random.nextInt(maxEndDiameter / 4);
	    }
	
	    boolean flag2 = false;
	
	    if (startDiameter == -1) {
	        startDiameter = endDiameter / 2;
	        flag2 = true;
	    }
	
	    int j = random.nextInt(endDiameter / 2) + endDiameter / 4;
	    boolean isRandomFlag = random.nextInt(6) == 0;
	
	    while (startDiameter < endDiameter) {
	    	//1.5 for the first term by default
	        double diameterMultiplier = 2.5 + (double)(Mth.sin((float)startDiameter * 3.1415927f / (float)endDiameter) * pitchModifier);
	        double tunnelDiameter = diameterMultiplier * length;
	        float yawChangeRate = Mth.cos(pitch);
	        float pitchChangeRateY = Mth.sin(pitch);
	        initialX += (double)(Mth.cos(yaw) * yawChangeRate);
	        initialY += (double)pitchChangeRateY;
	        initialZ += (double)(Mth.sin(yaw) * yawChangeRate);
	        pitch = isRandomFlag ? (pitch *= 0.92f) : (pitch *= 0.7f);
	        pitch += pitchChangeRate * 0.1f;
	        yaw += pitchChange * 0.1f;
	        pitchChangeRate *= 0.9f;
	        pitchChange *= 0.75f;
	        pitchChangeRate += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
	        pitchChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
	
	        if (!flag2 && startDiameter == j && pitchModifier > 1.0f && endDiameter > 0) {
	        	/*
	        	 * 
	    CarvingContext context,
	    CaveCarverConfiguration configuration,
	    Function<BlockPos, Holder<Biome>> biomeFunction,
	    long seed,
	    Aquifer aquifer,
	        	 */
	            this.addTunnel12(
	            		context, configuration, biomeFunction, random.nextLong(), aquifer, chunkPrimer, initialX, initialY, initialZ, random.nextFloat() * 0.5f + 0.5f, pitch - 1.5707964f, pitchModifier / 3.0f, startDiameter, endDiameter, 1.0, carvingMask);
	            this.addTunnel12(
	            		context, configuration, biomeFunction, random.nextLong(), aquifer, chunkPrimer, initialX, initialY, initialZ, random.nextFloat() * 0.5f + 0.5f, pitch + 1.5707964f, pitchModifier / 3.0f, startDiameter, endDiameter, 1.0, carvingMask);
	            return;
	        }
	
	        if (flag2 || random.nextInt(4) != 0) {
	            double deltaX = initialX - startX;
	            double deltaZ = initialZ - startZ;
	            double deltaDiameter = endDiameter - startDiameter;
	            double maxDiameter = pitchModifier + 2.0f + 16.0f;
	
	            if (deltaX * deltaX + deltaZ * deltaZ - deltaDiameter * deltaDiameter > maxDiameter * maxDiameter) {
	                return;
	            }
	            
	            mbPosCheckAir.set(initialX, initialY, initialZ);
	            boolean flagInAir = chunkPrimer.getBlockState(mbPosCheckAir).isAir();
	            if(flagInAir) {
	            	airPosList.add(new BlockPos((int) initialX, (int) initialY, (int) initialZ));
	            }
	
	            if (initialX >= startX - 16.0 - diameterMultiplier * 2.0 && initialZ >= startZ - 16.0 - diameterMultiplier * 2.0 && initialX <= startX + 16.0 + diameterMultiplier * 2.0 && initialZ <= startZ + 16.0 + diameterMultiplier * 2.0) {
	                int minX = Mth.floor(initialX - diameterMultiplier) - minBlockX - 1;
	                int maxX = Mth.floor(initialX + diameterMultiplier) - minBlockX + 1;
	                
	                int minY = Mth.floor(initialY - tunnelDiameter) - 1;
	                int maxY = Mth.floor(initialY + tunnelDiameter) + 1;
	                
	                int minZ = Mth.floor(initialZ - diameterMultiplier) - minBlockZ - 1;
	                int maxZ = Mth.floor(initialZ + diameterMultiplier) - minBlockZ + 1;
	
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
	
	                    if (flag2) break;
	                }
	            }
	            
	        ++startDiameter;
	        }
	    }
	
	}







}
