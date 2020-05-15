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
        center.getBlock().setType(Material.GOLD_BLOCK);
        for (int i = 0; i < 1; i++) {
            Utils.getCylBlocks(center, i + 6, 1, false).forEach(block -> block.setType(Material.DIAMOND_BLOCK));
        }
        Bukkit.broadcastMessage("Hellow i am a ball 18");
    }
}
