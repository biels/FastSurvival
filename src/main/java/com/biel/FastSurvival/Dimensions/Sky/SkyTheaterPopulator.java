package com.biel.FastSurvival.Dimensions.Sky;

import com.biel.FastSurvival.Utils.Cuboid;
import com.biel.FastSurvival.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Random;

public class SkyTheaterPopulator {
    public void populate(World world, Random random, Chunk source) {
        if (!(random.nextInt(90) <= 1)) {
            return;
        }
    }
    public void generate(Location center) {
        center = center.toVector().toBlockVector().add(new Vector(0.5, 0.5, 0.5)).toLocation(center.getWorld());
        int height = Utils.NombreEntre(8, 13);
        for (int i = 0; i < height; i++) {
            center.add(0,1, 0);
            if (i >= height - 4){
                Utils.getCylBlocks(center,3*(height - 4) + height - 8 ,1,false).forEach(block -> block.setType(Material.QUARTZ_BLOCK));
                Bukkit.broadcastMessage("height:" + height);
                Bukkit.broadcastMessage("i:" + i);
            }
            else {
                Utils.getCylBlocks(center, i*3 + 6, 1,false).forEach(block -> block.setType(Material.QUARTZ_BLOCK));
                Utils.getCylBlocks(center, i*3 + 7, 1,false).forEach(block -> block.setType(Material.QUARTZ_BLOCK));
                Utils.getCylBlocks(center, i*3 + 8, 1,false).forEach(block -> block.setType(Material.QUARTZ_BLOCK));
                Utils.getCylBlocks(center, i*3 + 3, 1,false).forEach(block -> block.setType(Material.QUARTZ_SLAB));
                if (i == 0)  Utils.getCylBlocks(center,5,1,true).forEach(block -> block.setType(Material.SMOOTH_QUARTZ));
            }
        }
    }
}
