package com.biel.FastSurvival.NetherStructures;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class FindNetherStructureLocation {
    private boolean notValid;
    private World world;
    private Random random;
    private Chunk source;
    private boolean debug;
    private int xw;
    private int zw;
    private Location center;

    public FindNetherStructureLocation(World world, Random random, Chunk source, boolean debug) {
        this.world = world;
        this.random = random;
        this.source = source;
        this.debug = debug;
    }

    boolean notValid() {
        return notValid;
    }

    public int getXw() {
        return xw;
    }

    public int getZw() {
        return zw;
    }

    public Location getCenter() {
        return center;
    }

    public FindNetherStructureLocation invoke(int xw, int zw, int minFilledCount) {
        if(debug){
            if (!(random.nextInt(60) <= 1)) {notValid = true; return this;}
            int centerX = (source.getX() << 4) + random.nextInt(16);
            int centerZ = (source.getZ() << 4) + random.nextInt(16);
            this.xw = xw;
            this.zw = zw;
            center = new Location(world, centerX + 0.5, world.getHighestBlockYAt(centerX, centerZ) + 0.5, centerZ + 0.5);
            notValid = false;
            return this;
        }
        if((source.getX() + source.getZ()) % 2 == 0){
            notValid = true;
            return this;
        }
        if (!(random.nextInt(30) <= 1)) {
            notValid = true;
            return this;
        }
        int centerX = (source.getX() << 4) + random.nextInt(16);
        int centerZ = (source.getZ() << 4) + random.nextInt(16);
        this.xw = xw;
        this.zw = zw;
        center = ChestCorePopulator.getFreeLocationAt(new Location(world, centerX, 0, centerZ));
        if(center == null || center.getY() > 120) {
            notValid = true;
            return this;
        }
        Vector offset = new Vector(0.5, (this.xw / 2.0) + 0.5, 0.5);
        center.add(offset);
        Location ensureAir = center.clone().add(0, this.xw + 1, 0);
        if(ensureAir.getBlock().getType() != Material.AIR) {
            notValid = true;
            return this;
        }
        Cuboid cuboidAround = Utils.getCuboidAround(center.clone().add(0, 1, 0), this.xw, 1, this.zw);
        List<Block> cuboidAroundBlocks = cuboidAround.getBlocks();
        int filledCount = 0;
        for (int i = 0; i < cuboidAroundBlocks.size(); i++) {
            if(cuboidAroundBlocks.get(i).getType() != Material.AIR){
                filledCount++;
            }
            if(filledCount > minFilledCount) {
                notValid = true;
                return this;
            }
        }
        notValid = false;
        return this;
    }
}
